package de.cookindustries.lib.spring.gui.hmi.svg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SVGPathTest
{

    private static final Double D_1  = 1.003;
    private static final Double D_10 = 10.003;

    @Test
    void test_pathCommand_A()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdA
                .builder()
                .rx(D_1)
                .ry(D_1)
                .angleLargeArcFlag(1)
                .sweepFlag(1)
                .endPointX(D_10)
                .endPointY(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdA
                .builder()
                .relative(true)
                .rx(D_1)
                .ry(D_1)
                .angleLargeArcFlag(1)
                .sweepFlag(1)
                .endPointX(D_10)
                .endPointY(D_10)
                .build();

        // run & verify
        assertEquals("A 1.003 1.003 1 1 10.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("a 1.003 1.003 1 1 10.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_C()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdC
                .builder()
                .controlPoint1X(D_10)
                .controlPoint1Y(D_10)
                .controlPoint2X(D_10)
                .controlPoint2Y(D_10)
                .endPointX(D_10)
                .endPointY(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdC
                .builder()
                .relative(true)
                .controlPoint1X(D_10)
                .controlPoint1Y(D_10)
                .controlPoint2X(D_10)
                .controlPoint2Y(D_10)
                .endPointX(D_10)
                .endPointY(D_10)
                .build();

        // run & verify
        assertEquals("C 10.003,10.003 10.003,10.003 10.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("c 10.003,10.003 10.003,10.003 10.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_H()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdH
                .builder()
                .x(D_1)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdH
                .builder()
                .relative(true)
                .x(D_1)
                .build();

        // run & verify
        assertEquals("H 1.003", cmdAbsolute.getCommandString());
        assertEquals("h 1.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_L()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdL
                .builder()
                .x(D_1)
                .y(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdL
                .builder()
                .relative(true)
                .x(D_1)
                .y(D_10)
                .build();

        // run & verify
        assertEquals("L 1.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("l 1.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_M()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdM
                .builder()
                .x(D_1)
                .y(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdM
                .builder()
                .relative(true)
                .x(D_1)
                .y(D_10)
                .build();

        // run & verify
        assertEquals("M 1.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("m 1.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_Q()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdQ
                .builder()
                .controlPointX(D_1)
                .controlPointY(D_10)
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdQ
                .builder()
                .relative(true)
                .controlPointX(D_1)
                .controlPointY(D_10)
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        // run & verify
        assertEquals("Q 1.003,10.003 1.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("q 1.003,10.003 1.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_S()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdS
                .builder()
                .controlPointX(D_1)
                .controlPointY(D_10)
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdS
                .builder()
                .relative(true)
                .controlPointX(D_1)
                .controlPointY(D_10)
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        // run & verify
        assertEquals("S 1.003,10.003 1.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("s 1.003,10.003 1.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_T()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdT
                .builder()
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdT
                .builder()
                .relative(true)
                .endPointX(D_1)
                .endPointY(D_10)
                .build();

        // run & verify
        assertEquals("T 1.003,10.003", cmdAbsolute.getCommandString());
        assertEquals("t 1.003,10.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_V()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdV
                .builder()
                .y(D_1)
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdV
                .builder()
                .relative(true)
                .y(D_1)
                .build();

        // run & verify
        assertEquals("V 1.003", cmdAbsolute.getCommandString());
        assertEquals("v 1.003", cmdRelative.getCommandString());
    }

    @Test
    void test_pathCommand_Z()
    {
        // setup
        SVGPathCommand cmdAbsolute =
            SVGPathCmdZ
                .builder()
                .build();

        SVGPathCommand cmdRelative =
            SVGPathCmdZ
                .builder()
                .relative(true)
                .build();

        // run & verify
        assertEquals("Z", cmdAbsolute.getCommandString());
        assertEquals("z", cmdRelative.getCommandString());
    }
}
