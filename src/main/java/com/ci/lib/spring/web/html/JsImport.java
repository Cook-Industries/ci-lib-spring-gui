package com.ci.lib.spring.web.html;

import lombok.Data;

@Data
public class JsImport
{

    private final String name;
    private final String src;

    @Override
    public String toString()
    {
        return String.format("\"%s\": \"%s\"", name, src);
    }
}
