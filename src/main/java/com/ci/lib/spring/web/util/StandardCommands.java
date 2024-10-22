/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.util;

import com.ci.lib.spring.web.hmi.ValuePair;
import com.ci.lib.spring.web.response.Action;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
public class StandardCommands
{

    private StandardCommands()
    {

    }

    public static String callStm(String requestUrl, Action action, ValuePair... valuePairs)
    {
        StringConcat sc = new StringConcat();

        sc.append("{");
        for (ValuePair vp : valuePairs)
        {
            sc.append(vp.getHtmlRep());
            sc.append(", ");
        }
        if (valuePairs.length > 0)
        {
            sc.removeLastChars(2);
        }
        sc.append("}");

        return String.format("call(\"%s\", \"%s\", %s)", requestUrl, action, sc.getString());
    }

    public static String requestModalStm(String requestUrl, ValuePair... valuePairs)
    {
        StringConcat sc = new StringConcat();

        sc.append("{");
        for (ValuePair vp : valuePairs)
        {
            sc.append(vp.getHtmlRep());
            sc.append(", ");
        }
        if (valuePairs.length > 0)
        {
            sc.removeLastChars(2);
        }
        sc.append("}");

        return String.format("requestModal(\"%s\", %s)", requestUrl, sc.getString());
    }

    public static String openSite(String requestUrl, ValuePair... valuePairs)
    {
        return openSite(requestUrl, null, valuePairs);
    }

    public static String openSite(String requestUrl, Integer uid, ValuePair... valuePairs)
    {
        StringConcat sc = new StringConcat();

        if (uid != null)
        {
            sc.append("?");
            sc.append(new ValuePair("id", uid).getURLRep());
            sc.append("&");
        }

        if (valuePairs.length > 0 && uid == null)
        {
            sc.append("?");
        }

        for (ValuePair vp : valuePairs)
        {
            sc.append(vp.getURLRep());
            sc.append("&");
        }

        if (valuePairs.length > 0)
        {
            sc.removeLastChars(1);
        }

        return String.format("openSite(\"%s\", \"%s\")", requestUrl, sc.getString());
    }
}
