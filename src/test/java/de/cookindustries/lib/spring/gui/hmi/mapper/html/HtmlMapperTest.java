/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.function.TagInputSettings;
import de.cookindustries.lib.spring.gui.hmi.container.AudioContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerItem;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonClass;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonIcon;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.Direction;
import de.cookindustries.lib.spring.gui.hmi.container.EmptyContainer;
import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.container.HeadingContainer;
import de.cookindustries.lib.spring.gui.hmi.container.HiddenContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ImageContainer;
import de.cookindustries.lib.spring.gui.hmi.container.LinkContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;
import de.cookindustries.lib.spring.gui.hmi.container.SVGContainer;
import de.cookindustries.lib.spring.gui.hmi.container.SplittedContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TextContainer;
import de.cookindustries.lib.spring.gui.hmi.input.Checkbox;
import de.cookindustries.lib.spring.gui.hmi.input.Date;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.Hidden;
import de.cookindustries.lib.spring.gui.hmi.input.Input;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.Password;
import de.cookindustries.lib.spring.gui.hmi.input.Radio;
import de.cookindustries.lib.spring.gui.hmi.input.Select;
import de.cookindustries.lib.spring.gui.hmi.input.Slider;
import de.cookindustries.lib.spring.gui.hmi.input.Switch;
import de.cookindustries.lib.spring.gui.hmi.input.Tag;
import de.cookindustries.lib.spring.gui.hmi.input.Textarea;
import de.cookindustries.lib.spring.gui.hmi.input.Textbox;
import de.cookindustries.lib.spring.gui.hmi.input.Textfield;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPath;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdA;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGText;

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
                .value("50")
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
                .value("50")
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
                .value("25")
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
                .value("500")
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
                .value("0.5")
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
                .value("150")
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
                .value("5")
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
                .value("50")
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
        assertTrue(
            html.contains("<span style=\"--ci-slider-index: 2\" class=\"input-slider-label input-slider-label-active\">medium</span>"));
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
                .value("500")
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
                .value("50")
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

    @Test
    void test_map_textfieldInput_rendersPattern()
    {
        // setup
        Textfield input =
            Textfield
                .builder()
                .uid("tf")
                .name("name")
                .submitAs("name")
                .mandatoryText("required")
                .pattern("[a-z]+")
                .build();
        String    html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("pattern=\"[a-z]+\""));
    }

    @Test
    void test_map_textfieldInput_withoutPattern_hasNoPatternAttribute()
    {
        // setup
        Textfield input =
            Textfield
                .builder()
                .uid("tf")
                .name("name")
                .submitAs("name")
                .mandatoryText("required")
                .pattern("")
                .build();
        String    html;

        // run
        html = mapInForm(input);

        // verify
        assertFalse(html.contains("pattern="));
    }

    @Test
    void test_map_textfieldInput_classesLandOnTheInput()
    {
        // setup
        Textfield input =
            Textfield
                .builder()
                .uid("tf")
                .name("name")
                .submitAs("name")
                .mandatoryText("required")
                .clazz("extra")
                .build();
        String    html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("class=\"form-control input-field extra\""));
    }

    @Test
    void test_map_dateInput_rendersMinMaxAndStep()
    {
        // setup
        Date   input =
            Date
                .builder()
                .uid("dt")
                .name("day")
                .submitAs("day")
                .mandatoryText("required")
                .value("2026-05-01")
                .min("2026-01-01")
                .max("2026-12-31")
                .step(7)
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("min=\"2026-01-01\""));
        assertTrue(html.contains("max=\"2026-12-31\""));
        assertTrue(html.contains("step=\"7\""));
    }

    @Test
    void test_map_dateInput_blankBoundsAreOmitted()
    {
        // setup
        Date   input =
            Date
                .builder()
                .uid("dt")
                .name("day")
                .submitAs("day")
                .mandatoryText("required")
                .value("2026-05-01")
                .min("")
                .max("")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertFalse(html.contains("min="));
        assertFalse(html.contains("max="));
        assertTrue(html.contains("step=\"1\""));
    }

    @Test
    void test_map_checkboxInput_disabled_disablesEveryBox()
    {
        // setup
        Checkbox input =
            Checkbox
                .builder()
                .uid("cb")
                .name("options")
                .submitAs("options")
                .mandatoryText("required")
                .disabled(true)
                .box(InputValue.builder().text("a").value("a").submitAs("a").build())
                .box(InputValue.builder().text("b").value("b").submitAs("b").build())
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertEquals(2, html.split("disabled=\"disabled\"", -1).length - 1);
    }

    @Test
    void test_map_checkboxInput_enabled_hasNoDisabledAttribute()
    {
        // setup
        Checkbox input =
            Checkbox
                .builder()
                .uid("cb")
                .name("options")
                .submitAs("options")
                .mandatoryText("required")
                .box(InputValue.builder().text("a").value("a").submitAs("a").build())
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertFalse(html.contains("disabled"));
    }

    @Test
    void test_map_checkboxInput_classesLandOnEveryBox()
    {
        // setup
        Checkbox input =
            Checkbox
                .builder()
                .uid("cb")
                .name("options")
                .submitAs("options")
                .mandatoryText("required")
                .clazz("extra")
                .box(InputValue.builder().text("a").value("a").submitAs("a").build())
                .box(InputValue.builder().text("b").value("b").submitAs("b").build())
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertEquals(2, html.split("class=\"form-checkbox-input extra\"", -1).length - 1);
    }

    @Test
    void test_map_radioInput_disabled_disablesEveryButton()
    {
        // setup
        Radio  input =
            Radio
                .builder()
                .uid("rd")
                .name("plan")
                .submitAs("plan")
                .mandatoryText("required")
                .disabled(true)
                .box(InputValue.builder().text("a").value("a").build())
                .box(InputValue.builder().text("b").value("b").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertEquals(2, html.split("disabled=\"disabled\"", -1).length - 1);
    }

    @Test
    void test_map_radioInput_rendersMandatoryAndInfoMarkers()
    {
        // setup
        Radio  input =
            Radio
                .builder()
                .uid("rd")
                .name("plan")
                .submitAs("plan")
                .mandatoryText("required")
                .mandatory(true)
                .infoText("pick one")
                .infoUrl("/info")
                .box(InputValue.builder().text("a").value("a").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"input-icon-form-mandatory-plan\""));
        assertTrue(html.contains("id=\"input-icon-form-info-plan\""));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info\""));
        assertTrue(html.contains("id=\"input-icon-form-error-plan\""));
        assertTrue(html.contains("id=\"input-icon-form-warning-plan\""));
    }

    @Test
    void test_map_radioInput_carriesClassesAndDataAttributes()
    {
        // setup
        Radio  input =
            Radio
                .builder()
                .uid("rd")
                .name("plan")
                .submitAs("plan")
                .mandatoryText("required")
                .clazz("extra")
                .dataAttribute("key", "value")
                .box(InputValue.builder().text("a").value("a").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("class=\"input-checkboxes-wrapper extra\""));
        assertTrue(html.contains("data-key=\"value\""));
        assertTrue(html.contains("data-submit-as=\"plan\""));
    }

    @Test
    void test_map_audioContainer_rendersLoopMutedAndPreload()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("au")
                .src("/a.mp3")
                .caption("caption")
                .looped(true)
                .muted(true)
                .preload(AudioContainer.Preload.METADATA)
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("loop=\"loop\""));
        assertTrue(html.contains("muted=\"muted\""));
        assertTrue(html.contains("preload=\"metadata\""));
    }

    @Test
    void test_map_audioContainer_defaultsAreNotLoopedNorMuted()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("au")
                .src("/a.mp3")
                .caption("caption")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("loop="));
        assertFalse(html.contains("muted="));
        assertTrue(html.contains("preload=\"auto\""));
    }

    @Test
    void test_map_audioContainer_carriesClassesAndDataAttributes()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("au")
                .src("/a.mp3")
                .caption("caption")
                .clazz("extra")
                .dataAttribute("key", "value")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-key=\"value\""));
    }

    @Test
    void test_map_burgerContainer_rendersIconAndButtonClass()
    {
        // setup
        BurgerContainer container =
            BurgerContainer
                .builder()
                .uid("bm")
                .icon("bi-three-dots")
                .btnClass(ButtonClass.DANGER)
                .build();
        String          html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"burger-menu btn-danger\""));
        assertTrue(html.contains("class=\"burger-icon bi bi-three-dots\""));
    }

    @Test
    void test_map_burgerContainer_defaultIcon()
    {
        // setup
        BurgerContainer container = BurgerContainer.builder().uid("bm").build();
        String          html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"burger-icon bi bi-list\""));
    }

    @Test
    void test_map_burgerContainer_itemIconSize_isRenderedOnlyWhenSet()
    {
        // setup
        BurgerContainer container =
            BurgerContainer
                .builder()
                .uid("bm")
                .item(BurgerItem.builder().text("sized").icon("bi-x").iconSize(3).url("/sized").build())
                .item(BurgerItem.builder().text("plain").icon("bi-y").url("/plain").build())
                .build();
        String          html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("style=\"font-size: 3rem\""));
        assertEquals(1, html.split("font-size", -1).length - 1);
    }

    @Test
    void test_map_buttonIcon_alt_rendersAriaLabel()
    {
        // setup
        ButtonIcon container =
            ButtonIcon
                .builder()
                .uid("bi")
                .icon("bi-x")
                .onClick("close()")
                .alt("close dialog")
                .build();
        String     html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("aria-label=\"close dialog\""));
    }

    @Test
    void test_map_buttonIcon_withoutAlt_hasNoAriaLabel()
    {
        // setup
        ButtonIcon container =
            ButtonIcon
                .builder()
                .uid("bi")
                .icon("bi-x")
                .onClick("close()")
                .build();
        String     html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("aria-label"));
    }

    @Test
    void test_map_contentContainer_direction_horizontal()
    {
        // setup
        ContentContainer container =
            ContentContainer
                .builder()
                .uid("ct")
                .direction(Direction.HORIZONTAL)
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"container-direction-row\""));
    }

    @Test
    void test_map_contentContainer_direction_verticalReverse()
    {
        // setup
        ContentContainer container =
            ContentContainer
                .builder()
                .uid("ct")
                .direction(Direction.VERTICAL_REVERSE)
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"container-direction-column-reverse\""));
    }

    @Test
    void test_map_contentContainer_withoutDirection_hasNoDirectionClass()
    {
        // setup
        ContentContainer container =
            ContentContainer
                .builder()
                .uid("ct")
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("container-direction"));
    }

    @Test
    void test_map_splittedContainer_direction_horizontalReverse()
    {
        // setup
        SplittedContainer container =
            SplittedContainer
                .builder()
                .uid("sp")
                .direction(Direction.HORIZONTAL_REVERSE)
                .head(TextContainer.builder().text("head").build())
                .center(List.of())
                .tail(TextContainer.builder().text("tail").build())
                .build();
        String            html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"d-flex justify-content-between container-direction-row-reverse\""));
    }

    @Test
    void test_map_formContainer_direction_reverse()
    {
        // setup
        FormContainer row    =
            FormContainer
                .builder()
                .uid("form-row")
                .direction(Direction.HORIZONTAL_REVERSE)
                .build();
        FormContainer column =
            FormContainer
                .builder()
                .uid("form-column")
                .direction(Direction.VERTICAL_REVERSE)
                .build();
        String        rowHtml;
        String        columnHtml;

        // run
        rowHtml = HtmlMapper.map(row);
        columnHtml = HtmlMapper.map(column);

        // verify
        assertTrue(rowHtml.contains("form-container-row-reverse"));
        assertTrue(columnHtml.contains("form-container-column-reverse"));
    }

    private String mapInForm(Input input)
    {
        return HtmlMapper
            .map(
                FormContainer
                    .builder()
                    .uid("form")
                    .input(input)
                    .build());
    }

    @Test
    void test_map_button_rendersAllProperties()
    {
        // setup
        Button container =
            Button
                .builder()
                .uid("b")
                .text("Click")
                .onClick("go()")
                .btnClass(ButtonClass.DANGER)
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"b\""));
        assertTrue(html.contains("onclick=\"go()\""));
        assertTrue(html.contains("class=\"btn btn-danger extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-tooltip=\"tip\""));
        assertTrue(html.contains(">Click</button>"));
    }

    @Test
    void test_map_button_withoutOnClick_hasNoOnclickAttribute()
    {
        // setup
        Button container =
            Button
                .builder()
                .uid("b")
                .text("Click")
                .onClick("")
                .build();
        String html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("onclick"));
    }

    @Test
    void test_map_emptyContainer_rendersNothing()
    {
        // setup
        EmptyContainer container =
            EmptyContainer
                .builder()
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertEquals("", html);
    }

    @Test
    void test_map_headingContainer_rendersAllProperties()
    {
        // setup
        HeadingContainer container =
            HeadingContainer
                .builder()
                .uid("h")
                .text("Title")
                .size(3)
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<h3 "));
        assertTrue(html.contains("id=\"h\""));
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-tooltip=\"tip\""));
        assertTrue(html.contains(">Title</h3>"));
    }

    @Test
    void test_map_headingContainer_escapesText()
    {
        // setup
        HeadingContainer container =
            HeadingContainer
                .builder()
                .uid("h")
                .text("a<b")
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<h1 id=\"h\">a&lt;b</h1>"));
    }

    @Test
    void test_map_hiddenContainer_rendersChildrenClassesAndDataAttributes()
    {
        // setup
        HiddenContainer container =
            HiddenContainer
                .builder()
                .uid("hc")
                .clazz("extra")
                .dataAttribute("k", "v")
                .child(TextContainer.builder().text("inner").build())
                .build();
        String          html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"hc\""));
        assertTrue(html.contains("class=\"hidden extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains(">inner</p>"));
    }

    @Test
    void test_map_imageContainer_rendersAllProperties()
    {
        // setup
        ImageContainer container =
            ImageContainer
                .builder()
                .uid("img")
                .src("a/b.png")
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<img "));
        assertTrue(html.contains("id=\"img\""));
        assertTrue(html.contains("src=\"a/b.png\""));
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-tooltip=\"tip\""));
        assertTrue(html.contains("/>"));
    }

    @Test
    void test_map_linkContainer_rendersAllProperties()
    {
        // setup
        LinkContainer container =
            LinkContainer
                .builder()
                .uid("l")
                .href("/go")
                .target("_blank")
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .content(TextContainer.builder().text("go").build())
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<a "));
        assertTrue(html.contains("id=\"l\""));
        assertTrue(html.contains("href=\"/go\""));
        assertTrue(html.contains("target=\"_blank\""));
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-tooltip=\"tip\""));
        assertTrue(html.contains(">go</p>"));
        assertTrue(html.contains("</a>"));
    }

    @Test
    void test_map_textContainer_rendersAllProperties()
    {
        // setup
        TextContainer container =
            TextContainer
                .builder()
                .uid("t")
                .text("some text")
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<p "));
        assertTrue(html.contains("id=\"t\""));
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-tooltip=\"tip\""));
        assertTrue(html.contains(">some text</p>"));
    }

    @Test
    void test_map_textContainer_inline_rendersSpan()
    {
        // setup
        TextContainer container =
            TextContainer
                .builder()
                .uid("t")
                .text("some text")
                .inline(true)
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<span id=\"t\">some text</span>"));
    }

    @Test
    void test_map_textContainer_escapesText()
    {
        // setup
        TextContainer container =
            TextContainer
                .builder()
                .uid("t")
                .text("a<b")
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<p id=\"t\">a&lt;b</p>"));
    }

    @Test
    void test_map_modalContainer_rendersAllProperties()
    {
        // setup
        ModalContainer container =
            ModalContainer
                .builder()
                .uid("m")
                .clazz("extra")
                .dataAttribute("k", "v")
                .name("Title")
                .closeOnOverlayClick(true)
                .btnNameLeft("Cancel")
                .btnClassLeft(ButtonClass.DANGER)
                .btnFunctionLeft("left()")
                .btnNameCenter("Mid")
                .btnClassCenter(ButtonClass.SUCCESS)
                .btnFunctionCenter("mid()")
                .btnNameRight("OK")
                .btnClassRight(ButtonClass.WARNING)
                .btnFunctionRight("right()")
                .content(TextContainer.builder().text("body").build())
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"m\""));
        assertTrue(html.contains("class=\"modal-body extra\""));
        assertTrue(html.contains("data-close-on-overlay=\"true\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("class=\"modal-name text-bold\">Title</p>"));
        assertTrue(html.contains("id=\"m-right-btn\" onclick=\"right()\" class=\"btn btn-warning\">OK</button>"));
        assertTrue(html.contains("id=\"m-center-btn\" onclick=\"mid()\" class=\"btn btn-success\">Mid</button>"));
        assertTrue(html.contains("id=\"m-left-btn\" onclick=\"left()\" class=\"btn btn-danger\">Cancel</button>"));
        assertTrue(html.contains(">body</p>"));
        assertTrue(html.indexOf("m-right-btn") < html.indexOf("m-center-btn"));
        assertTrue(html.indexOf("m-center-btn") < html.indexOf("m-left-btn"));
    }

    @Test
    void test_map_modalContainer_withoutOptionalButtons()
    {
        // setup
        ModalContainer container =
            ModalContainer
                .builder()
                .uid("m")
                .name("Title")
                .btnNameRight("OK")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"m-right-btn\""));
        assertFalse(html.contains("m-center-btn"));
        assertFalse(html.contains("m-left-btn"));
        assertTrue(html.contains("data-close-on-overlay=\"false\""));
    }

    @Test
    void test_map_contentContainer_rendersChildrenInOrder()
    {
        // setup
        ContentContainer container =
            ContentContainer
                .builder()
                .uid("c")
                .content(TextContainer.builder().text("first").build())
                .content(TextContainer.builder().text("second").build())
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains(">first</p>"));
        assertTrue(html.contains(">second</p>"));
        assertTrue(html.indexOf("first") < html.indexOf("second"));
    }

    @Test
    void test_map_contentContainer_carriesClassesAndDataAttributes()
    {
        // setup
        ContentContainer container =
            ContentContainer
                .builder()
                .uid("c")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String           html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"c\""));
        assertTrue(html.contains("class=\"extra\""));
        assertTrue(html.contains("data-k=\"v\""));
    }

    @Test
    void test_map_formContainer_carriesClassesAndDataAttributes()
    {
        // setup
        FormContainer container =
            FormContainer
                .builder()
                .uid("f")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String        html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"f\""));
        assertTrue(html.contains("class=\"form-container form-container-column extra\""));
        assertTrue(html.contains("data-k=\"v\""));
    }

    @Test
    void test_map_splittedContainer_carriesClassesAndDataAttributes()
    {
        // setup
        SplittedContainer container =
            SplittedContainer
                .builder()
                .uid("s")
                .clazz("extra")
                .dataAttribute("k", "v")
                .head(TextContainer.builder().text("head").build())
                .center(List.of(TextContainer.builder().text("center").build()))
                .tail(TextContainer.builder().text("tail").build())
                .build();
        String            html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("id=\"s\""));
        assertTrue(html.contains("class=\"d-flex justify-content-between extra\""));
        assertTrue(html.contains("data-k=\"v\""));
    }

    @Test
    void test_map_audioContainer_controlsAreRenderedByDefault()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("a")
                .src("s.mp3")
                .caption("cap")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("controls=\"controls\""));
    }

    @Test
    void test_map_audioContainer_withoutControls_omitsAttribute()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("a")
                .src("s.mp3")
                .caption("cap")
                .controls(false)
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertFalse(html.contains("controls"));
    }

    @Test
    void test_map_audioContainer_autoplay()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("a")
                .src("s.mp3")
                .caption("cap")
                .autoplay(true)
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("autoplay=\"autoplay\""));
    }

    @Test
    void test_map_burgerContainer_itemMethod()
    {
        // setup
        BurgerContainer container =
            BurgerContainer
                .builder()
                .uid("bm")
                .item(BurgerItem.builder().text("t").icon("bi-x").url("/u").method("GET").build())
                .item(BurgerItem.builder().text("p").icon("bi-y").url("/p").build())
                .build();
        String          html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("data-burger-url=\"/u\""));
        assertTrue(html.contains("data-burger-method=\"GET\""));
        assertTrue(html.contains("data-burger-method=\"POST\""));
    }

    @Test
    void test_map_buttonIcon_btnClass()
    {
        // setup
        ButtonIcon container =
            ButtonIcon
                .builder()
                .uid("bi")
                .icon("bi-x")
                .onClick("go()")
                .btnClass(ButtonClass.WARNING)
                .build();
        String     html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("class=\"btn btn-icon btn-warning\""));
    }

    @Test
    void test_map_textfieldInput_rendersAllProperties()
    {
        // setup
        Textfield input =
            Textfield
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .value("init")
                .placeholder("ph")
                .maxCharacters(20)
                .pattern("[a-z]+")
                .onKeydown("key()")
                .onEnterPress("enter()")
                .build();
        String    html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("value=\"init\""));
        assertTrue(html.contains("placeholder=\"ph\""));
        assertTrue(html.contains("data-max-characters=\"20\""));
        assertTrue(html.contains("pattern=\"[a-z]+\""));
        assertTrue(html.contains("onkeydown=\"key()\""));
        assertTrue(html.contains("data-on-enter-press=\"enter()\""));
    }

    @Test
    void test_map_numberInput_rendersAllProperties()
    {
        // setup
        Number input =
            Number
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .value(5)
                .min(1)
                .max(10)
                .step(2)
                .placeholder("ph")
                .onEnterPress("enter()")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("value=\"5\""));
        assertTrue(html.contains("min=\"1\""));
        assertTrue(html.contains("max=\"10\""));
        assertTrue(html.contains("step=\"2\""));
        assertTrue(html.contains("placeholder=\"ph\""));
        assertTrue(html.contains("data-on-enter-press=\"enter()\""));
    }

    @Test
    void test_map_passwordInput_rendersAllProperties()
    {
        // setup
        Password input =
            Password
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .placeholder("pw")
                .onEnterPress("enter()")
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("value=\"\""));
        assertTrue(html.contains("placeholder=\"pw\""));
        assertTrue(html.contains("data-on-enter-press=\"enter()\""));
    }

    @Test
    void test_map_fileInput_rendersAllProperties()
    {
        // setup
        File   input =
            File
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .multiple(true)
                .accepts(List.of(".png", ".pdf"))
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("multiple=\"multiple\""));
        assertTrue(html.contains("accept=\".png,.pdf\""));
    }

    @Test
    void test_map_fileInput_notMultiple_hasNoMultipleAttribute()
    {
        // setup
        File   input =
            File
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .multiple(false)
                .accepts(List.of(".png"))
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertFalse(html.contains("multiple"));
    }

    @Test
    void test_map_selectInput_rendersAllProperties()
    {
        // setup
        Select input =
            Select
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .selected("b")
                .values(List.of(InputValue.builder().text("A").value("a").tooltip("tip a").build(),
                    InputValue.builder().text("B").value("b").build()))
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("<select "));
        assertTrue(html.contains("<option value=\"a\" data-tooltip=\"tip a\">A</option>"));
        assertTrue(html.contains("<option value=\"b\" selected=\"selected\">B</option>"));
    }

    @Test
    void test_map_tagInput_rendersAllProperties()
    {
        // setup
        Tag    input =
            Tag
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .tags(List.of("t1", "t2"))
                .pattern("[a-z]+")
                .settings(TagInputSettings.builder().id("in").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("value=\"t1,t2\""));
        assertTrue(html.contains("pattern=\"[a-z]+\""));
    }

    @Test
    void test_map_tagInput_blankPattern_hasNoPatternAttribute()
    {
        // setup
        Tag    input =
            Tag
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .pattern("")
                .settings(TagInputSettings.builder().id("in").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertFalse(html.contains("pattern="));
    }

    @Test
    void test_map_textareaInput_rendersAllProperties()
    {
        // setup
        Textarea input =
            Textarea
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .value("body")
                .placeholder("ph")
                .maxCharacters(50)
                .onKeydown("key()")
                .rows(3)
                .cols(20)
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
        assertTrue(html.contains("<textarea "));
        assertTrue(html.contains(">body</textarea>"));
        assertTrue(html.contains("placeholder=\"ph\""));
        assertTrue(html.contains("data-max-characters=\"50\""));
        assertTrue(html.contains("onkeydown=\"key()\""));
        assertTrue(html.contains("rows=\"3\""));
        assertTrue(html.contains("cols=\"20\""));
    }

    @Test
    void test_map_textareaInput_defaults()
    {
        // setup
        Textarea input =
            Textarea
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("rows=\"5\""));
        assertFalse(html.contains("cols="));
    }

    @Test
    void test_map_dateInput_rendersBaseProperties()
    {
        // setup
        Date   input =
            Date
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .value("2026-05-01")
                .min("2026-01-01")
                .max("2026-12-31")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("disabled=\"disabled\""));
        assertTrue(html.contains("input-field extra"));
    }

    @Test
    void test_map_hiddenInput_rendersAllProperties()
    {
        // setup
        Hidden input =
            Hidden
                .builder()
                .uid("hid")
                .name("")
                .submitAs("sa")
                .mandatoryText("")
                .value("secret")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"hid\""));
        assertTrue(html.contains("value=\"secret\""));
        assertTrue(html.contains("class=\"hidden extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("data-value-type=\"HIDDEN\""));
    }

    @Test
    void test_map_textboxInput_rendersAllProperties()
    {
        // setup
        Textbox input =
            Textbox
                .builder()
                .uid("tb")
                .name("label")
                .text("line")
                .tooltip("tip")
                .clazz("extra")
                .dataAttribute("k", "v")
                .build();
        String  html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"tb\""));
        assertTrue(html.contains("class=\"text-preserve-newline extra\""));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains(">line</div>"));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
    }

    @Test
    void test_map_switchInput_rendersBaseProperties()
    {
        // setup
        Switch input =
            Switch
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .checked(true)
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("form-switch-input input-field extra"));
    }

    @Test
    void test_map_sliderInput_rendersBaseProperties()
    {
        // setup
        Slider input =
            Slider
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .value("50")
                .min(BigInteger.valueOf(0))
                .max(BigInteger.valueOf(100))
                .step(BigInteger.valueOf(5))
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("id=\"in\""));
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("id=\"input-icon-form-info-sa-text\""));
        assertTrue(html.contains("form-slider-input extra"));
    }

    @Test
    void test_map_checkboxInput_rendersBaseProperties()
    {
        // setup
        Checkbox input =
            Checkbox
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .mandatory(true)
                .disabled(true)
                .tooltip("tip")
                .onInput("inp()")
                .infoText("info")
                .infoUrl("/info/url")
                .clazz("extra")
                .dataAttribute("k", "v")
                .box(InputValue.builder().text("a").value("a").submitAs("a").build())
                .box(InputValue.builder().text("b").value("b").submitAs("b").build())
                .build();
        String   html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("data-k=\"v\""));
        assertTrue(html.contains("data-submit-as=\"sa-a\""));
        assertTrue(html.contains("data-submit-id=\"form\""));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("id=\"input-icon-form-mandatory-sa-text\" class=\"input-icon-text tooltip-text\">required</div>"));
        assertTrue(html.contains("class=\"input-icon-text tooltip-text\">info</div>"));
        assertTrue(html.contains("data-fetch-input-info-url=\"/info/url\""));
        assertTrue(html.contains("form-checkbox-input extra"));
        assertTrue(html.contains("disabled=\"disabled\""));
    }

    @Test
    void test_map_radioInput_rendersTooltipAndOnInput()
    {
        // setup
        Radio  input =
            Radio
                .builder()
                .uid("rd")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .tooltip("tip")
                .onInput("inp()")
                .box(InputValue.builder().text("a").value("a").tooltip("box tip").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("data-tooltip=\"tip\">label</label>"));
        assertTrue(html.contains("oninput=\"inp()\""));
        assertTrue(html.contains("data-tooltip=\"box tip\""));
    }

    @Test
    void test_map_radioInput_escapesBoxText()
    {
        // setup
        Radio  input =
            Radio
                .builder()
                .uid("rd")
                .name("plan")
                .submitAs("plan")
                .mandatoryText("required")
                .box(InputValue.builder().text("a<b").value("a").build())
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains("a&lt;b</label>"));
    }

    @Test
    void test_map_selectInput_escapesOptionText()
    {
        // setup
        Select input =
            Select
                .builder()
                .uid("in")
                .name("label")
                .submitAs("sa")
                .mandatoryText("required")
                .values(List.of(InputValue.builder().text("a<b").value("a").build()))
                .build();
        String html;

        // run
        html = mapInForm(input);

        // verify
        assertTrue(html.contains(">a&lt;b</option>"));
    }

    @Test
    void test_map_audioContainer_escapesCaption()
    {
        // setup
        AudioContainer container =
            AudioContainer
                .builder()
                .uid("au")
                .src("/a.mp3")
                .caption("a<b")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("a&lt;b</figcaption>"));
    }

    @Test
    void test_map_modalContainer_escapesNameOnce()
    {
        // setup
        ModalContainer container =
            ModalContainer
                .builder()
                .uid("m")
                .name("Löschen & Co")
                .btnNameRight("OK")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains(">L&ouml;schen &amp; Co</p>"));
        assertFalse(html.contains("&amp;ouml;"));
    }

    @Test
    void test_map_modalContainer_escapesButtonNamesOnce()
    {
        // setup
        ModalContainer container =
            ModalContainer
                .builder()
                .uid("m")
                .name("Title")
                .btnNameRight("Übernehmen")
                .btnNameCenter("a<b")
                .btnFunctionCenter("mid()")
                .btnNameLeft("Schließen")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains(">&Uuml;bernehmen</button>"));
        assertTrue(html.contains(">a&lt;b</button>"));
        assertTrue(html.contains(">Schlie&szlig;en</button>"));
        assertFalse(html.contains("&amp;"));
    }

    @Test
    void test_map_modalContainer_centerButtonWithoutFunction()
    {
        // setup
        ModalContainer container =
            ModalContainer
                .builder()
                .uid("m")
                .name("Title")
                .btnNameRight("OK")
                .btnNameCenter("Mid")
                .build();
        String         html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("<button id=\"m-center-btn\" class=\"btn "));
        assertTrue(html.contains(">Mid</button>"));
    }

    @Test
    void test_map_svgText_rendersTextContent()
    {
        // setup
        SVGContainer container =
            SVGContainer
                .builder()
                .uid("svg")
                .width(100)
                .height(50)
                .element(
                    SVGText
                        .builder()
                        .uid("t")
                        .text("a<b")
                        .dy(2d)
                        .textLength(10d)
                        .build())
                .build();
        String       html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains(">a&lt;b</text>"));
        assertTrue(html.contains("dy=\"2.000\""));
        assertTrue(html.contains("textLength=\"10.000\""));
        assertFalse(html.contains("y2="));
    }

    @Test
    void test_map_svgPath_arcWithXAxisRotation()
    {
        // setup
        SVGContainer container =
            SVGContainer
                .builder()
                .uid("svg")
                .width(100)
                .height(50)
                .element(
                    SVGPath
                        .builder()
                        .uid("p")
                        .command(
                            SVGPathCmdA
                                .builder()
                                .rx(5d)
                                .ry(5d)
                                .xAxisRotation(30d)
                                .angleLargeArcFlag(true)
                                .endPointX(10d)
                                .endPointY(10d)
                                .build())
                        .build())
                .build();
        String       html;

        // run
        html = HtmlMapper.map(container);

        // verify
        assertTrue(html.contains("d=\"A 5.000 5.000 30.000 1 0 10.000,10.000\""));
    }
}
