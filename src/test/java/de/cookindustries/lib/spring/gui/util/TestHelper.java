package de.cookindustries.lib.spring.gui.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

public final class TestHelper
{

    public static void multiAssert(List<Runnable> asserts)
    {
        List<AssertionError> errors = new ArrayList<>();

        asserts.forEach(ass -> {
            try
            {
                ass.run();
            }
            catch (AssertionError err)
            {
                errors.add(err);
            }

            if (!errors.isEmpty())
            {
                StringBuilder sb = new StringBuilder("Multiple assertion failures:\n");

                errors.forEach(err -> {
                    sb
                        .append(" - ")
                        .append(err.getMessage())
                        .append("\n");
                });

                fail(sb.toString());
            }
        });
    }

}
