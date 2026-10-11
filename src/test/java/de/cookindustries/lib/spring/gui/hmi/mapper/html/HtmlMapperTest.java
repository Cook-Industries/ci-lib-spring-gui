/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.input.Slider;
import de.cookindustries.lib.spring.gui.hmi.input.Switch;
import de.cookindustries.lib.spring.gui.hmi.input.Textfield;

class HtmlMapperTest
{

    @Test
    void test_map_switchInput_rendersCheckboxWithSwitchRole()
    {
        // setup
        Switch        input     =
            Switch
                .builder()
                .uid("sw")
                .name("notifications")
                .submitAs("notifications")
                .mandatoryText("required")
                .checked(false)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"sw\""));
        assertTrue(html.contains("type=\"checkbox\""));
        assertTrue(html.contains("role=\"switch\""));
        assertTrue(html.contains("form-switch-input"));
    }

    @Test
    void test_map_switchInput_carriesSubmitData()
    {
        // setup
        Switch        input     =
            Switch
                .builder()
                .uid("sw")
                .name("notifications")
                .submitAs("notifications")
                .mandatoryText("required")
                .checked(false)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("data-submit-as=\"notifications\""));
        assertTrue(html.contains("data-value-type=\"SWITCH\""));
    }

    @Test
    void test_map_switchInput_isWrappedInSwitchInputContainer()
    {
        // setup
        Switch        input     =
            Switch
                .builder()
                .uid("sw")
                .name("notifications")
                .submitAs("notifications")
                .mandatoryText("required")
                .checked(false)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<div class=\"input-container input-container-switch\">"));
        assertTrue(html.contains("<label for=\"sw\""));
        assertTrue(html.contains("id=\"input-icon-form-warning-notifications\""));
        assertTrue(html.contains("id=\"input-icon-form-error-notifications\""));
        assertTrue(html.contains("id=\"input-icon-form-info-notifications\""));
    }

    @Test
    void test_map_switchInput_checkedAndDisabled()
    {
        // setup
        Switch        input     =
            Switch
                .builder()
                .uid("sw")
                .name("notifications")
                .submitAs("notifications")
                .mandatoryText("required")
                .checked(true)
                .disabled(true)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("checked=\"checked\""));
        assertTrue(html.contains("disabled=\"disabled\""));
    }

    @Test
    void test_map_switchInput_uncheckedAndEnabled()
    {
        // setup
        Switch        input     =
            Switch
                .builder()
                .uid("sw")
                .name("notifications")
                .submitAs("notifications")
                .mandatoryText("required")
                .checked(false)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("checked"));
        assertFalse(html.contains("disabled"));
    }

    @Test
    void test_map_switchInput_mandatoryMarkerOnlyWhenMandatory()
    {
        // setup
        Switch        mandatory =
            Switch
                .builder()
                .uid("sw-mandatory")
                .name("must")
                .submitAs("must")
                .mandatoryText("required")
                .mandatory(true)
                .checked(false)
                .build();
        Switch        optional  =
            Switch
                .builder()
                .uid("sw-optional")
                .name("may")
                .submitAs("may")
                .mandatoryText("required")
                .checked(false)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(mandatory)
                .input(optional)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"input-icon-form-mandatory-must\""));
        assertFalse(html.contains("id=\"input-icon-form-mandatory-may\""));
    }

    @Test
    void test_map_inputWithoutAdditionalClass_hasPlainInputContainer()
    {
        // setup
        Textfield     input     =
            Textfield
                .builder()
                .uid("tf")
                .name("name")
                .submitAs("name")
                .mandatoryText("required")
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<div class=\"input-container\">"));
    }

    @Test
    void test_map_sliderInput_rendersRangeWithBounds()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(50))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(5))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"sl\""));
        assertTrue(html.contains("type=\"range\""));
        assertTrue(html.contains("value=\"50\""));
        assertTrue(html.contains("min=\"0\""));
        assertTrue(html.contains("max=\"100\""));
        assertTrue(html.contains("step=\"5\""));
        assertTrue(html.contains("form-slider-input"));
        assertTrue(html.contains("data-value-type=\"SLIDER\""));
    }

    @Test
    void test_map_sliderInput_showsMinCurrentAndMax()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(50))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(5))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<span class=\"input-slider-min\">0</span>"));
        assertTrue(html.contains("<output for=\"sl\" class=\"input-slider-value\">50</output>"));
        assertTrue(html.contains("<span class=\"input-slider-max\">100</span>"));
    }

    @Test
    void test_map_sliderInput_ratioAndTicksFollowValueAndStep()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(25))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(10))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 0.2500; --ci-slider-ticks: 10"));
    }

    @Test
    void test_map_sliderInput_manyStepsFallBackToTenTicks()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(500))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(1000))
                .step(BigInteger.valueOf(1))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 0.5000; --ci-slider-ticks: 10"));
    }

    @Test
    void test_map_sliderInput_decimalValuesKeepTheirScale()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(new BigDecimal("0.5"))
                .min(new BigDecimal("0.0"))
                .max(new BigDecimal("1.0"))
                .step(new BigDecimal("0.1"))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("value=\"0.5\""));
        assertTrue(html.contains("min=\"0.0\""));
        assertTrue(html.contains("max=\"1.0\""));
        assertTrue(html.contains("step=\"0.1\""));
        assertTrue(html.contains("--ci-slider-ratio: 0.5000; --ci-slider-ticks: 10"));
    }

    @Test
    void test_map_sliderInput_valueAboveMaxClampsRatio()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(150))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(10))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 1; --ci-slider-ticks: 10"));
    }

    @Test
    void test_map_sliderInput_emptyRangeHasNoRatioAndSingleTick()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(5))
                .min(BigInteger.valueOf(5))
                .max(BigInteger.valueOf(5))
                .step(BigInteger.valueOf(1))
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 0; --ci-slider-ticks: 1"));
    }

    @Test
    void test_map_sliderInput_disabled()
    {
        // setup
        Slider        input     =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(50))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(5))
                .disabled(true)
                .build();
        FormContainer container =
            FormContainer
                .builder()
                .uid("form")
                .input(input)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("disabled=\"disabled\""));
    }

    @Test
    void test_map_sliderInput_customValues_runOverTheirIndexes()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .customValues(List.of("off", "low", "medium", "high"))
                .value("medium")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("value=\"2\""));
        assertTrue(html.contains("min=\"0\""));
        assertTrue(html.contains("max=\"3\""));
        assertTrue(html.contains("step=\"1\""));
        assertTrue(html.contains("aria-valuetext=\"medium\""));
        assertTrue(html.contains("--ci-slider-ratio: 0.6667; --ci-slider-ticks: 3"));
        assertFalse(html.contains("input-slider-min"));
        assertFalse(html.contains("input-slider-value"));
        assertFalse(html.contains("input-slider-max"));
    }

    @Test
    void test_map_sliderInput_customValues_showEachValueAsLabel()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .customValues(List.of("off", "low", "medium", "high"))
                .value("medium")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("class=\"input-slider input-slider-custom\""));
        assertTrue(html.contains("<span style=\"--ci-slider-index: 0\" class=\"input-slider-label\">off</span>"));
        assertTrue(html.contains("<span style=\"--ci-slider-index: 1\" class=\"input-slider-label\">low</span>"));
        assertTrue(html.contains("<span style=\"--ci-slider-index: 2\" class=\"input-slider-label input-slider-label-active\">medium</span>"));
        assertTrue(html.contains("<span style=\"--ci-slider-index: 3\" class=\"input-slider-label\">high</span>"));
    }

    @Test
    void test_map_sliderInput_customValues_escapeLabels()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .customValues(List.of("<b>", "a & b"))
                .value("<b>")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains(">&lt;b&gt;</span>"));
        assertTrue(html.contains(">a &amp; b</span>"));
        assertTrue(html.contains("aria-valuetext=\"&lt;b&gt;\""));
    }

    @Test
    void test_map_sliderInput_marks_overrideTheFallbackTickCount()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(500))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(1000))
                .step(BigInteger.valueOf(1))
                .marks(5)
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 0.5000; --ci-slider-ticks: 4"));
    }

    @Test
    void test_map_sliderInput_marks_overrideTheStepBasedTickCount()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("sl")
                .name("level")
                .submitAs("level")
                .mandatoryText("required")
                .value(BigInteger.valueOf(50))
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(5))
                .marks(3)
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("--ci-slider-ratio: 0.5000; --ci-slider-ticks: 2"));
    }

    private String mapInForm(Slider input)
    {
        return HtmlMapper
            .map(
                FormContainer
                    .builder()
                    .uid("form")
                    .input(input)
                    .build());
    }

}
