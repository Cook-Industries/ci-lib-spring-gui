/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ci.lib.spring.web.hmi.input.Marker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PseudoElement
{

    private String              uid        = null;

    private String              type;

    private List<String>        classes    = new ArrayList<>();

    private Map<String, String> attributes = new HashMap<>();

    private Map<String, String> parameters = new HashMap<>();

    private List<Marker>        marker     = new ArrayList<>();

    private List<PseudoElement> children   = new ArrayList<>();

}
