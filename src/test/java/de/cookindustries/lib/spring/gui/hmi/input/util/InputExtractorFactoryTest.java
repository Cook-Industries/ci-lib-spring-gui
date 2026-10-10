/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.HANDLER_KEY;
import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.MAX_BATCH;
import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.MAX_FILE;
import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.UPLOAD_URL;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.cookindustries.lib.spring.gui.function.BasicUiFunction;
import de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.Fixture;
import de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.TestForm;
import de.cookindustries.lib.spring.gui.i18n.BasicText;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.response.ResponseAction;
import de.cookindustries.lib.spring.gui.response.StartFileUploadResponse;
import de.cookindustries.lib.spring.gui.response.message.ActivateMarkerMessage;
import de.cookindustries.lib.spring.gui.response.message.MessageType;
import de.cookindustries.lib.spring.gui.util.file.UploadBatchState;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionState;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionStore.FileInit;

class InputExtractorFactoryTest
{

    private static final String           KEY = "files";

    private Fixture                       fx;

    private MultiValueMap<String, String> inputs;

    @BeforeEach
    void init()
    {
        fx = new Fixture();
        inputs = new LinkedMultiValueMap<>();
        inputs.add("__form_id", "testForm");
    }

    private InputExtractor<TestForm> create()
    {
        return fx.factory.create(inputs, Locale.ENGLISH, () -> new TestForm("x"));
    }

    private static String announce(String name, long size)
    {
        return "[{\"name\":\"" + name + "\",\"size\":" + size + "}]";
    }

    @Test
    void test_create_setsLocale()
    {
        // setup
        Supplier<TestForm>       supplier = () -> new TestForm("x");
        InputExtractor<TestForm> result;

        // run
        result = fx.factory.create(inputs, Locale.GERMAN, supplier);

        // verify
        assertSame(Locale.GERMAN, result.getLocale());
    }

    @Test
    void test_create_missingFormId_isRejected()
    {
        // setup
        inputs.clear();

        // run & verify
        assertThrows(IllegalArgumentException.class, this::create);
    }

    @Test
    void test_create_appliesTheConfiguredFileLimit()
    {
        // setup
        inputs.add(KEY, announce("a.bin", MAX_FILE + 1));
        InputExtractor<TestForm> extractor = create();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertEquals(1, extractor.getMarker().size());
        assertArrayEquals(new Object[] { KEY, MAX_FILE }, extractor.getMarker().get(0).getTranslationPlaceholders());
    }

    @Test
    void test_create_appliesTheConfiguredBatchLimit()
    {
        // setup
        inputs.add("first", announce("a.bin", MAX_FILE));
        inputs.add("second", announce("b.bin", MAX_BATCH - MAX_FILE + 1));
        InputExtractor<TestForm> extractor = create();

        // run
        extractor.checkFiles("first", false, false).checkFiles("second", false, false);

        // verify
        assertEquals(1, extractor.getMarker().size());
        assertEquals("second", extractor.getMarker().get(0).getTransferId());
        assertArrayEquals(new Object[] { "second", MAX_BATCH }, extractor.getMarker().get(0).getTranslationPlaceholders());
    }

    @Test
    void test_create_returnsIndependentExtractors()
    {
        // setup
        InputExtractor<TestForm> first  = create();
        InputExtractor<TestForm> second = create();

        // run
        first.checkFiles(KEY, false, false);

        // verify
        assertTrue(first.hasMarker());
        assertFalse(second.hasMarker());
    }

    @Test
    void test_approve_withFiles_startsTheUploadThroughTheCoordinator()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);
        Response                 result;

        // run
        result = extractor.approve(HANDLER_KEY, () -> new TestForm("x"));

        // verify
        StartFileUploadResponse start = assertInstanceOf(StartFileUploadResponse.class, result);
        UploadSessionState      state;

        assertEquals(ResponseAction.FILE_UPLOAD, start.getAction());
        assertEquals(UPLOAD_URL, start.getUploadUrl());
        assertSame(fx.limits, start.getUploadLimits());
        assertSame(fx.policy, start.getUploadPolicy());
        assertEquals(1, start.getFieldDescribtors().size());

        state = start.getFieldDescribtors().get(0);

        assertEquals(start.getBatchUuid(), state.getBatchUuid());
        assertEquals(KEY, state.getFieldId());
        assertEquals("a.png", state.getFilename());
        assertEquals(10L, state.getExpectedLength());
    }

    @Test
    void test_approve_withFiles_persistsBatchAndFilesInTheStore()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);
        Response                 result;

        // run
        result = extractor.approve(HANDLER_KEY, () -> new TestForm("x"));

        // verify
        StartFileUploadResponse start = assertInstanceOf(StartFileUploadResponse.class, result);
        FileInit                init;

        assertNotNull(fx.store.batch);
        assertEquals(start.getBatchUuid(), fx.store.batch.batchUuid());
        assertEquals(UploadBatchState.OPEN, fx.store.batch.status());
        assertEquals(HANDLER_KEY, fx.store.batch.completeKey());
        assertEquals(fx.policy.getPartLength(), fx.store.batch.partLength());
        assertTrue(new String(fx.store.batch.encryptedFormPojo(), StandardCharsets.UTF_8).contains("\"name\":\"x\""));
        assertEquals(1, fx.store.files.size());

        init = fx.store.files.get(0);

        assertEquals(start.getFieldDescribtors().get(0).getUuid(), init.fileUuid());
        assertEquals(KEY, init.fieldId());
        assertEquals(10L, init.expectedLength());
    }

    @Test
    void test_approve_withMarkers_doesNotTouchTheStore()
    {
        // setup
        inputs.add(KEY, announce("a.png", MAX_FILE + 1));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);
        Response                 result;

        // run
        result = extractor.approve(HANDLER_KEY, () -> new TestForm("x"));

        // verify
        assertInstanceOf(NotificationResponse.class, result);
        assertNull(fx.store.batch);
        assertNull(fx.store.files);
    }

    @Test
    void test_approve_unknownHandlerKey_isRejected()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.approve("unknown", () -> new TestForm("x")));
        assertNull(fx.store.batch);
    }

    @Test
    void test_approve_formOfWrongType_isRejected()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.approve(HANDLER_KEY, () -> "not a form"));
        assertNull(fx.store.batch);
    }

    @Test
    void test_failedCheckResponse_carriesOneMarkerMessagePerMarker()
    {
        // setup
        inputs.add("second", announce("a.png", 1));
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false).checkFiles("second", false, false);
        Response                 result;

        extractor.checkAndConsumeAsNotEmptyString("name", v -> {
        });

        // run
        result = extractor.approve(data -> {
        }, () -> null);

        // verify
        NotificationResponse  response = assertInstanceOf(NotificationResponse.class, result);
        ActivateMarkerMessage files;
        ActivateMarkerMessage name;

        assertEquals(ResponseAction.NOTIFICATION, response.getAction());
        assertEquals(2, response.getMessages().size());

        files = assertInstanceOf(ActivateMarkerMessage.class, response.getMessages().get(0));
        name = assertInstanceOf(ActivateMarkerMessage.class, response.getMessages().get(1));

        assertEquals(KEY, files.getFieldId());
        assertEquals("name", name.getFieldId());
    }

    @Test
    void test_failedCheckResponse_describesTheMarkerForTheUi()
    {
        // setup
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);
        Response                 result;

        // run
        result = extractor.approve(HANDLER_KEY, () -> new TestForm("x"));

        // verify
        NotificationResponse  response = assertInstanceOf(NotificationResponse.class, result);
        ActivateMarkerMessage message  = assertInstanceOf(ActivateMarkerMessage.class, response.getMessages().get(0));

        assertEquals("testForm", message.getFormId());
        assertEquals(MarkerCategory.ERROR, message.getCategory());
        assertEquals(KEY, message.getFieldId());
        assertEquals(MessageType.ERROR, message.getType());
        assertEquals("input-icon-testForm-error-" + KEY, message.getUid());
        assertEquals(BasicText.INPUT_CHECK_RESULT_EXPECTED_BUT_NOT_SET.getText(), message.getText());
    }

    @Test
    void test_failedCheckResponse_hidesTheGlobalLoader()
    {
        // setup
        InputExtractor<TestForm> extractor = create().checkFiles(KEY, false, false);
        Response                 result;

        // run
        result = extractor.approve(HANDLER_KEY, () -> new TestForm("x"));

        // verify
        NotificationResponse response = assertInstanceOf(NotificationResponse.class, result);

        assertTrue(response.getCalls().stream().anyMatch(call -> call == BasicUiFunction.HIDE_GLOBAL_LOADER));
    }

    @ParameterizedTest
    @EnumSource(value = InputCheckResultType.class, mode = EnumSource.Mode.EXCLUDE, names = "PASS")
    void test_translation_everyFailingResultType_isDefined(InputCheckResultType type)
    {
        // setup
        String text;

        // run
        text = fx.translations.getText(Locale.ENGLISH, type.getTranslationKey(), "a", "b", "c");

        // verify
        assertFalse(text.startsWith("I18N ["), () -> "missing translation for " + type);
    }
}
