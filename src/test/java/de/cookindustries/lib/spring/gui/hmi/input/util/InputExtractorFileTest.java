/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

/**
 * Files are announced by the browser as a json list of name and size, the bytes are not part of the request.
 */
class InputExtractorFileTest
{

    // private static final long MAX_FILE = 1_000L;

    // private static final long MAX_BATCH = 1_500L;

    // private MultiValueMap<String, String> inputs;

    // private record DataObj()
    // {
    // }

    // @BeforeEach
    // void setUp()
    // {
    // inputs = new LinkedMultiValueMap<>();

    // inputs.add("__form_id", "testForm");
    // }

    // /**
    // * Announce files for a field like the browser does: {@code announce("files", "a.txt", 10, "b.txt", 20)}.
    // */
    // private void announce(String key, Object... namesAndSizes)
    // {
    // List<String> files = new ArrayList<>();

    // for (int i = 0; i < namesAndSizes.length; i += 2)
    // {
    // files.add("{\"name\":\"" + namesAndSizes[i] + "\",\"size\":" + namesAndSizes[i + 1] + "}");
    // }

    // inputs.add(key, "[" + String.join(",", files) + "]");
    // }

    // private InputExtractor<DataObj> limited(UploadStarter starter)
    // {
    // return new InputExtractor<DataObj>(null, inputs, Locale.ENGLISH, starter, MAX_FILE, MAX_BATCH, DataObj::new);
    // }

    // @Test
    // void test_checkFiles_nullFile()
    // {
    // // setup
    // inputs.add("files", "[]");

    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(null, inputs, Locale.ENGLISH, DataObj::new);

    // // run
    // extractor.checkFiles("files", true, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_fieldMissing_nullable()
    // {
    // // setup
    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", true, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_noFile_notNullable()
    // {
    // // setup
    // inputs.add("files", "[]");

    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", false, false);

    // // verify
    // assertEquals(Boolean.TRUE, extractor.hasMarker());
    // assertEquals(1, extractor.getMarker().size());
    // }

    // @Test
    // void test_checkFiles_singleFile_allowed()
    // {
    // // setup
    // announce("files", "testfile.txt", 19);

    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", false, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_MultipleFile_allowed()
    // {
    // // setup
    // announce("files", "testfile.txt", 19, "testfile.txt", 19);

    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", false, true);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_MultipleFile_notAllowed()
    // {
    // // setup
    // announce("files", "testfile.txt", 19, "testfile.txt", 19);

    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", false, false);

    // // verify
    // assertEquals(Boolean.TRUE, extractor.hasMarker());
    // assertEquals(1, extractor.getMarker().size());
    // }

    // @Test
    // void test_checkFiles_fileAtLimit_allowed()
    // {
    // // setup
    // announce("files", "big.bin", MAX_FILE);

    // InputExtractor<DataObj> extractor = limited(null);

    // // run
    // extractor.checkFiles("files", false, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_fileTooLarge()
    // {
    // // setup
    // announce("files", "small.bin", 10, "big.bin", MAX_FILE + 1);

    // InputExtractor<DataObj> extractor = limited(null);

    // // run
    // extractor.checkFiles("files", false, true);

    // // verify
    // assertEquals(Boolean.TRUE, extractor.hasMarker());
    // assertEquals(1, extractor.getMarker().size());
    // }

    // @Test
    // void test_checkFiles_batchTooLarge_acrossFields()
    // {
    // // setup
    // announce("first", "a.bin", 900);
    // announce("second", "b.bin", 900);

    // InputExtractor<DataObj> extractor = limited(null);

    // // run
    // extractor.checkFiles("first", false, false);
    // extractor.checkFiles("second", false, false);

    // // verify: the first field fits, the second one pushes the form over the batch limit
    // assertEquals(Boolean.TRUE, extractor.hasMarker());
    // assertEquals(1, extractor.getMarker().size());
    // }

    // @Test
    // void test_checkFiles_batchAtLimit_acrossFields()
    // {
    // // setup
    // announce("first", "a.bin", 750);
    // announce("second", "b.bin", 750);

    // InputExtractor<DataObj> extractor = limited(null);

    // // run
    // extractor.checkFiles("first", false, false);
    // extractor.checkFiles("second", false, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_noLimits_hugeFileAllowed()
    // {
    // // setup
    // announce("files", "huge.bin", 1_000_000_000_000_000L);

    // InputExtractor<DataObj> extractor = new InputExtractor(inputs, Locale.ENGLISH);

    // // run
    // extractor.checkFiles("files", false, false);

    // // verify
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_malformedAnnouncement_isRejected()
    // {
    // // setup
    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // inputs.add("notJson", "hello");
    // inputs.add("notAList", "{\"name\":\"a.txt\",\"size\":1}");
    // inputs.add("negativeSize", "[{\"name\":\"a.txt\",\"size\":-5}]");
    // inputs.add("noName", "[{\"size\":5}]");

    // // run + verify: a broken client is not a user error and raises no marker
    // assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles("notJson", true, true));
    // assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles("notAList", true, true));
    // assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles("negativeSize", true, true));
    // assertThrows(IllegalArgumentException.class, () -> extractor.checkFiles("noName", true, true));
    // assertEquals(Boolean.FALSE, extractor.hasMarker());
    // }

    // @Test
    // void test_checkFiles_sameKeyTwice_isRejected()
    // {
    // // setup
    // InputExtractor<DataObj> extractor = new InputExtractor<DataObj>(inputs, Locale.ENGLISH);

    // extractor.checkFiles("files", true, true);

    // // run + verify
    // assertThrows(IllegalStateException.class, () -> extractor.checkFiles("files", true, true));
    // }

    // @Test
    // void test_approve_noFiles_returnsWithoutBuildingTheForm()
    // {
    // // setup
    // AtomicBoolean built = new AtomicBoolean(false);
    // AtomicBoolean started = new AtomicBoolean(false);

    // inputs.add("files", "[]");

    // InputExtractor<DataObj> extractor = limited((key, form, files) -> {
    // started.set(true);

    // return null;
    // });

    // extractor.checkFiles("files", true, true);

    // // run
    // extractor.approve("contact", () -> {
    // built.set(true);

    // return "FORM";
    // });

    // // verify
    // assertEquals(Boolean.FALSE, built.get());
    // assertEquals(Boolean.FALSE, started.get());
    // }

    // @Test
    // void test_approve_files_startsTheUpload()
    // {
    // // setup
    // StartResult result = new StartResult(UUID.randomUUID(), null, List.of());
    // List<Object> received = new ArrayList<>();

    // announce("files", "a.txt", 10, "b.txt", 20);

    // InputExtractor<DataObj> extractor = limited((key, form, files) -> {
    // received.add(key);
    // received.add(form);
    // received.add(files);

    // return result;
    // });

    // extractor.checkFiles("files", false, true);

    // // run
    // FileUploadStartException ex = assertThrows(FileUploadStartException.class, () -> extractor.approve("contact", () -> "FORM"));

    // // verify
    // assertSame(result, ex.getResult());
    // assertEquals("contact", ex.getCompletionHandlerKey());
    // assertEquals("contact", received.get(0));
    // assertEquals("FORM", received.get(1));
    // assertEquals(
    // List.of(new FileDeclaration("files", "a.txt", 10), new FileDeclaration("files", "b.txt", 20)),
    // received.get(2));
    // }

    // @Test
    // void test_approve_markers_throwWithoutBuildingTheForm()
    // {
    // // setup
    // AtomicBoolean built = new AtomicBoolean(false);
    // AtomicBoolean started = new AtomicBoolean(false);

    // announce("files", "big.bin", MAX_FILE + 1);

    // InputExtractor<DataObj> extractor = limited((key, form, files) -> {
    // started.set(true);

    // return null;
    // });

    // extractor.checkFiles("files", false, false);

    // // run + verify
    // assertThrows(InputExtractionException.class, () -> extractor.approve("contact", () -> {
    // built.set(true);

    // return "FORM";
    // }));
    // assertEquals(Boolean.FALSE, built.get());
    // assertEquals(Boolean.FALSE, started.get());
    // }

    // @Test
    // void test_approve_plain_withFiles_isRefused()
    // {
    // // setup
    // announce("files", "a.txt", 10);

    // InputExtractor<DataObj> extractor = limited((key, form, files) -> null);

    // extractor.checkFiles("files", false, false);

    // // run + verify
    // assertThrows(IllegalStateException.class, extractor::approve);
    // }

    // @Test
    // void test_approve_plain_withoutFiles_passes()
    // {
    // // setup
    // InputExtractor extractor = new InputExtractor(inputs, Locale.ENGLISH);

    // extractor.checkFiles("files", true, false);

    // // run + verify
    // extractor.approve();
    // }

    // @Test
    // void test_approve_files_withoutStarter_isRefused()
    // {
    // // setup
    // announce("files", "a.txt", 10);

    // InputExtractor extractor = new InputExtractor(inputs, Locale.ENGLISH);

    // extractor.checkFiles("files", false, false);

    // // run + verify
    // assertThrows(IllegalStateException.class, () -> extractor.approve("contact", () -> "FORM"));
    // }
}
