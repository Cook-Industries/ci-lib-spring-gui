/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.MAX_BATCH;
import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.MAX_FILE;
import static de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.UPLOAD_URL;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.cookindustries.lib.spring.gui.hmi.input.util.ExtractorTestSupport.Fixture;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor.UploadStarter;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.response.ResponseAction;
import de.cookindustries.lib.spring.gui.response.StartFileUploadResponse;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.FileDeclaration;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.StartResult;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionState;

class InputExtractorFileTest
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

    private static String announce(Object... namesAndSizes)
    {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < namesAndSizes.length; i += 2)
        {
            if (i > 0)
            {
                json.append(',');
            }

            json.append("{\"name\":\"").append(namesAndSizes[i]).append("\",\"size\":").append(namesAndSizes[i + 1]).append('}');
        }

        return json.append(']').toString();
    }

    private InputExtractor<Object> limited(UploadStarter starter)
    {
        return new InputExtractor<>(fx.factory, inputs, Locale.ENGLISH, starter, MAX_FILE, MAX_BATCH, () -> null);
    }

    private InputExtractor<Object> limited()
    {
        return limited(null);
    }

    private InputExtractor<Object> unlimited()
    {
        return new InputExtractor<>(fx.factory, inputs, Locale.ENGLISH, () -> null);
    }

    private static InputCheckMarker singleMarker(InputExtractor<?> extractor)
    {
        assertEquals(1, extractor.getMarker().size());

        return extractor.getMarker().get(0);
    }

    private static void assertMarker(InputCheckMarker marker, String key, InputCheckResultType type, Object... placeholders)
    {
        assertEquals("testForm", marker.getFormId());
        assertEquals(key, marker.getTransferId());
        assertEquals(MarkerCategory.ERROR, marker.getCategory());
        assertEquals(type.getMarkerType(), marker.getType());
        assertEquals(type.getTranslationKey(), marker.getTranslationKey());
        assertArrayEquals(placeholders, marker.getTranslationPlaceholders());
    }

    @Test
    void test_checkFiles_returnsThis()
    {
        // setup
        InputExtractor<Object> extractor = limited();
        InputExtractor<Object> result;

        // run
        result = extractor.checkFiles(KEY, true, true);

        // verify
        assertSame(extractor, result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t" })
    void test_checkFiles_blankKey_isRejected(String key)
    {
        // setup
        InputExtractor<Object> extractor = limited();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles(key, true, true));
    }

    @Test
    void test_checkFiles_nullKey_isRejected()
    {
        // setup
        InputExtractor<Object> extractor = limited();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles(null, true, true));
    }

    @Test
    void test_checkFiles_sameKeyTwice_isRejected()
    {
        // setup
        InputExtractor<Object> extractor = limited().checkFiles(KEY, true, true);

        // run & verify
        assertThrows(IllegalStateException.class, () -> extractor.checkFiles(KEY, true, true));
    }

    @Test
    void test_checkFiles_sameKeyTwice_isRejectedEvenAfterAMarker()
    {
        // setup
        InputExtractor<Object> extractor = limited().checkFiles(KEY, false, true);

        // run & verify
        assertThrows(IllegalStateException.class, () -> extractor.checkFiles(KEY, false, true));
    }

    @Test
    void test_checkFiles_fieldMissing_nullable_passes()
    {
        // setup
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, true, true);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_fieldMissing_notNullable_raisesEmptyButExpected()
    {
        // setup
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.EMPTY_BUT_EXPECTED, KEY);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "[]", " [ ] " })
    void test_checkFiles_emptyAnnouncement_nullable_passes(String announcement)
    {
        // setup
        inputs.add(KEY, announcement);
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, true, true);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "[]", " [ ] " })
    void test_checkFiles_emptyAnnouncement_notNullable_raisesEmptyButExpected(String announcement)
    {
        // setup
        inputs.add(KEY, announcement);
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.EMPTY_BUT_EXPECTED, KEY);
    }

    @Test
    void test_checkFiles_singleFile_passes()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_multipleFiles_allowed_pass()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10, "b.png", 20, "c.png", 30));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_multipleFiles_notAllowed_raisesTooMany()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10, "b.png", 20));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_MANY, KEY);
    }

    @Test
    void test_checkFiles_tooManyWins_overTooLarge()
    {
        // setup
        inputs.add(KEY, announce("a.png", MAX_FILE + 1, "b.png", MAX_FILE + 1));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_MANY, KEY);
    }

    @Test
    void test_checkFiles_fileAtLimit_passes()
    {
        // setup
        inputs.add(KEY, announce("a.png", MAX_FILE));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_fileOverLimit_raisesTooLargeWithFileLimit()
    {
        // setup
        inputs.add(KEY, announce("a.png", MAX_FILE + 1));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_LARGE, KEY, MAX_FILE);
    }

    @Test
    void test_checkFiles_oneOversizedFileAmongMany_raisesSingleMarker()
    {
        // setup
        inputs.add(KEY, announce("a.png", 1, "b.png", MAX_FILE + 1, "c.png", 1));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_LARGE, KEY, MAX_FILE);
    }

    @Test
    void test_checkFiles_zeroSizedFile_passes()
    {
        // setup
        inputs.add(KEY, announce("empty.txt", 0));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_batchAtLimit_passes()
    {
        // setup
        inputs.add(KEY, announce("a.bin", MAX_FILE, "b.bin", MAX_BATCH - MAX_FILE));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_batchOverLimitWithinField_raisesTooLargeWithBatchLimit()
    {
        // setup
        inputs.add(KEY, announce("a.bin", MAX_FILE, "b.bin", MAX_BATCH - MAX_FILE + 1));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_LARGE, KEY, MAX_BATCH);
    }

    @Test
    void test_checkFiles_batchOverLimitAcrossFields_marksTheLaterField()
    {
        // setup
        inputs.add("first", announce("a.bin", 900));
        inputs.add("second", announce("b.bin", 700));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles("first", false, false).checkFiles("second", false, false);

        // verify
        assertMarker(singleMarker(extractor), "second", InputCheckResultType.TOO_LARGE, "second", MAX_BATCH);
    }

    @Test
    void test_checkFiles_batchAtLimitAcrossFields_passes()
    {
        // setup
        inputs.add("first", announce("a.bin", 900));
        inputs.add("second", announce("b.bin", 600));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles("first", false, false).checkFiles("second", false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_failedField_doesNotCountTowardsBatch()
    {
        // setup
        inputs.add("first", announce("a.bin", MAX_FILE + 1));
        inputs.add("second", announce("b.bin", MAX_FILE));
        inputs.add("third", announce("c.bin", MAX_BATCH - MAX_FILE));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles("first", false, false).checkFiles("second", false, false).checkFiles("third", false, false);

        // verify
        assertMarker(singleMarker(extractor), "first", InputCheckResultType.TOO_LARGE, "first", MAX_FILE);
    }

    @Test
    void test_checkFiles_sizeOverflow_saturatesInsteadOfWrapping()
    {
        // setup
        inputs.add(KEY, announce("a.bin", Long.MAX_VALUE, "b.bin", Long.MAX_VALUE));
        InputExtractor<Object> extractor = new InputExtractor<>(fx.factory, inputs, Locale.ENGLISH, null, Long.MAX_VALUE, Long.MAX_VALUE - 1, () -> null);

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_LARGE, KEY, Long.MAX_VALUE - 1);
    }

    @Test
    void test_checkFiles_withoutUploadSetup_hasNoLimits()
    {
        // setup
        inputs.add(KEY, announce("huge.bin", Long.MAX_VALUE - 1));
        InputExtractor<Object> extractor = unlimited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_zeroLimits_rejectEverythingNonEmpty()
    {
        // setup
        inputs.add(KEY, announce("a.bin", 1));
        InputExtractor<Object> extractor = new InputExtractor<>(fx.factory, inputs, Locale.ENGLISH, null, 0, 0, () -> null);

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertMarker(singleMarker(extractor), KEY, InputCheckResultType.TOO_LARGE, KEY, 0L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "not json",
        "{}",
        "{\"name\":\"a.png\",\"size\":1}",
        "\"text\"",
        "42",
        "null",
        "[1]",
        "[null]",
        "[{}]",
        "[{\"name\":\"a.png\"}]",
        "[{\"size\":1}]",
        "[{\"name\":\"a.png\",\"size\":\"1\"}]",
        "[{\"name\":\"a.png\",\"size\":-1}]",
        "[{\"name\":\"a.png\",\"size\":99999999999999999999}]",
        "[{\"name\":\"\",\"size\":1}]",
        "[{\"name\":\" \",\"size\":1}]",
        "[{\"name\":7,\"size\":1}]",
        "[{\"name\":null,\"size\":1}]",
        "[{\"name\":\"a.png\",\"size\":null}]",
        "[{\"name\":\"a.png\",\"size\":1},{\"name\":\"b.png\"}]" })
    void test_checkFiles_malformedAnnouncement_isRejectedWithoutMarker(String announcement)
    {
        // setup
        inputs.add(KEY, announcement);
        InputExtractor<Object> extractor = limited();

        // run & verify
        assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles(KEY, true, true));
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_additionalAnnouncedProperties_areIgnored()
    {
        // setup
        inputs.add(KEY, "[{\"name\":\"a.png\",\"size\":1,\"type\":\"image/png\",\"lastModified\":123}]");
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, false);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_onlyFirstValueOfTheKeyIsRead()
    {
        // setup
        inputs.add(KEY, announce("a.png", 1));
        inputs.add(KEY, "not json");
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles(KEY, false, true);

        // verify
        assertFalse(extractor.hasMarker());
    }

    @Test
    void test_checkFiles_independentFields_raiseTheirOwnMarkers()
    {
        // setup
        inputs.add("first", announce("a.png", 1, "b.png", 2));
        InputExtractor<Object> extractor = limited();

        // run
        extractor.checkFiles("first", false, false).checkFiles("second", false, false);

        // verify
        assertEquals(2, extractor.getMarker().size());
        assertMarker(extractor.getMarker().get(0), "first", InputCheckResultType.TOO_MANY, "first");
        assertMarker(extractor.getMarker().get(1), "second", InputCheckResultType.EMPTY_BUT_EXPECTED, "second");
    }

    @Test
    void test_approveConsumer_withAnnouncedFiles_isRejected()
    {
        // setup
        inputs.add(KEY, announce("a.png", 1));
        AtomicInteger          handled   = new AtomicInteger();
        InputExtractor<Object> extractor = limited().checkFiles(KEY, false, false);

        // run & verify
        assertThrows(IllegalStateException.class, () -> extractor.approve(data -> handled.incrementAndGet(), () -> null));
        assertEquals(0, handled.get());
    }

    @Test
    void test_approveConsumer_withFilesRejectedByTheChecks_returnsFailedCheckResponse()
    {
        // setup
        inputs.add(KEY, announce("a.png", MAX_FILE + 1));
        InputExtractor<Object> extractor = limited().checkFiles(KEY, false, false);
        Response               result;

        // run
        result = extractor.approve(data -> {
        }, () -> null);

        // verify
        assertInstanceOf(NotificationResponse.class, result);
    }

    @Test
    void test_approveConsumer_withoutAnnouncedFiles_runsHandler()
    {
        // setup
        AtomicInteger          handled   = new AtomicInteger();
        Response               expected  = NotificationResponse.builder().build();
        InputExtractor<Object> extractor = limited().checkFiles(KEY, true, true);
        Response               result;

        // run
        result = extractor.approve(data -> handled.incrementAndGet(), () -> expected);

        // verify
        assertSame(expected, result);
        assertEquals(1, handled.get());
    }

    @Test
    void test_approveKey_nullArguments_areRejected()
    {
        // setup
        InputExtractor<Object> extractor = limited();

        // run & verify
        assertThrows(NullPointerException.class, () -> extractor.approve((String) null, () -> "form"));
        assertThrows(NullPointerException.class, () -> extractor.approve("handler", (Supplier<?>) null));
    }

    @Test
    void test_approveKey_passesKeySupplierAndDeclarationsToTheStarter()
    {
        // setup
        inputs.add("first", announce("a.png", 10, "b.png", 20));
        inputs.add("second", announce("c.png", 30));
        List<Object[]>         calls     = new ArrayList<>();
        Supplier<String>       supplier  = () -> "form";
        UploadStarter          starter   = (key, form, files) -> {
                                             calls.add(new Object[] { key, form, files });

                                             return startResult(files);
                                         };
        InputExtractor<Object> extractor = limited(starter).checkFiles("first", false, true).checkFiles("second", false, false);

        // run
        extractor.approve("handler", supplier);

        // verify
        assertEquals(1, calls.size());
        assertEquals("handler", calls.get(0)[0]);
        assertSame(supplier, calls.get(0)[1]);
        assertEquals(
            List
                .of(
                    new FileDeclaration("first", "a.png", 10),
                    new FileDeclaration("first", "b.png", 20),
                    new FileDeclaration("second", "c.png", 30)),
            calls.get(0)[2]);
    }

    @Test
    void test_approveKey_declarationsPassedToTheStarterAreImmutable()
    {
        // setup
        inputs.add(KEY, announce("a.png", 1));
        List<List<FileDeclaration>> seen      = new ArrayList<>();
        InputExtractor<Object>      extractor =
            limited((key, form, files) -> {
                seen.add(files);

                return startResult(files);
            }).checkFiles(KEY, false, false);

        // run
        extractor.approve("handler", () -> "form");

        // verify
        assertThrows(UnsupportedOperationException.class, () -> seen.get(0).add(new FileDeclaration(KEY, "x", 1)));
    }

    @Test
    void test_approveKey_rejectedFieldDeclaresNothing()
    {
        // setup
        inputs.add("first", announce("a.png", MAX_FILE + 1));
        inputs.add("second", announce("b.png", 5));
        List<FileDeclaration>  seen      = new ArrayList<>();
        InputExtractor<Object> extractor =
            limited((key, form, files) -> {
                seen.addAll(files);

                return startResult(files);
            }).checkFiles("first", false, false).checkFiles("second", false, false);

        // run
        extractor.approve("handler", () -> "form");

        // verify
        assertTrue(seen.isEmpty());
    }

    @Test
    void test_approveKey_withMarkers_skipsTheStarterAndReturnsFailedCheckResponse()
    {
        // setup
        AtomicInteger          started   = new AtomicInteger();
        InputExtractor<Object> extractor =
            limited((key, form, files) -> {
                started.incrementAndGet();

                return startResult(files);
            }).checkFiles(KEY, false, false);
        Response               result;

        // run
        result = extractor.approve("handler", () -> "form");

        // verify
        assertEquals(0, started.get());
        assertInstanceOf(NotificationResponse.class, result);
    }

    @Test
    void test_approveKey_buildsStartResponseFromTheStartResult()
    {
        // setup
        inputs.add(KEY, announce("a.png", 10, "b.png", 20));
        StartResult[]          started   = new StartResult[1];
        InputExtractor<Object> extractor =
            limited((key, form, files) -> {
                started[0] = startResult(files);

                return started[0];
            }).checkFiles(KEY, false, true);
        Response               result;

        // run
        result = extractor.approve("handler", () -> "form");

        // verify
        StartFileUploadResponse start = assertInstanceOf(StartFileUploadResponse.class, result);

        assertEquals(ResponseAction.FILE_UPLOAD, start.getAction());
        assertEquals(started[0].batchUuid(), start.getBatchUuid());
        assertEquals(UPLOAD_URL, start.getUploadUrl());
        assertSame(started[0].policy(), start.getUploadPolicy());
        assertSame(fx.limits, start.getUploadLimits());
        assertEquals(started[0].files(), start.getFieldDescribtors());
        assertEquals(2, start.getFieldDescribtors().size());
    }

    @Test
    void test_approveKey_withoutUploadSupport_failsWithIllegalState()
    {
        // setup
        inputs.add(KEY, announce("a.png", 1));
        InputExtractor<Object> extractor = unlimited().checkFiles(KEY, false, false);

        // run & verify
        assertThrows(IllegalStateException.class, () -> extractor.approve("handler", () -> "form"));
    }

    private StartResult startResult(List<FileDeclaration> files)
    {
        UUID                batch  = UUID.randomUUID();
        List<UploadSessionState> states = new ArrayList<>();

        for (FileDeclaration file : files)
        {
            states
                .add(
                    UploadSessionState
                        .builder()
                        .uuid(UUID.randomUUID())
                        .batchUuid(batch)
                        .fieldId(file.fieldId())
                        .filename(file.filename())
                        .created(java.time.Instant.now())
                        .expectedLength(file.length())
                        .build());
        }

        return new StartResult(batch, fx.policy, states);
    }
}
