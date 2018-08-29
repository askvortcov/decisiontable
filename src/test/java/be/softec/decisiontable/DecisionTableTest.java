package be.softec.decisiontable;


import be.softec.decisiontable.data.Colour;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

import static be.softec.decisiontable.Tree.TreeBuilder.node;
import static be.softec.decisiontable.data.Colour.c;


public class DecisionTableTest {

    private static final String FALLBACK = "Fallback";

    private static final Tree<String> CARS =
            //@formatter:off
            node("Cars")
                .child(
                        node("Germany")
                            .child("BMW")
                            .child("Mercedes")
                )
                .child(
                        node("Japan")
                            .child("Toyota")
                            .child(
                                    node("Mazda")
                                        .child("CX5")
                                        .child("6")
                                        .child("CX3")
                            )
                )
                .child(
                        node("US")
                            .child("Ford")
                            .child("Tesla")
                )
                .build();
            //@formatter:on


    private static final Colour GRAY_SCALE = c("GrayScale", null);
    private static final Colour SIMPLE_WHITE = c("White", Color.WHITE);
    private static final Colour DARK_RED = c("Dark red", Color.RED.darker().darker());
    private static final Colour BLUES = c("Blues", Color.BLUE);
    private static final Colour GREEN = c("Green", Color.GREEN);

    private static final Tree<Colour> colors =
            //@formatter:off
            node(c("Colors", null))
                    .child(
                        node(GRAY_SCALE)
                            .child(SIMPLE_WHITE)
                            .child(c("Black", Color.BLACK))
                    )
                .child(
                        node(c("Reds", Color.RED))
                            .child(c("Light red",Color.RED.brighter().brighter()))
                            .child(DARK_RED)
                )
                .child(
                        node(BLUES)
                            .child(c("Light blue",Color.BLUE.brighter().brighter()))

                )
                .child(
                        node(GREEN)
                )
                .build();
            //@formatter:on

    @Test
    public void testSingleDomainDecision() {

        DecisionTable<String> decisionTable = DecisionTable.of(FALLBACK, CARS)
                .addRow("Konnichiwa", "Japan")
                .addRow("Zoom-zoom", "CX5")
                .addRow("Electric", "Tesla")
                .build();
        Assert.assertEquals("Zoom-zoom", decisionTable.accept("CX5"));

    }

    @Test
    public void testDoubleDomainDecision() {
        DecisionTable<String> decisionTable = DecisionTable.of(FALLBACK, CARS, colors)
                .addRow("Cool ones", "Japan", DARK_RED)
                .addRow("Zoom-zoom", "CX5", GRAY_SCALE)
                .addRow("Tricky one", "Japan", SIMPLE_WHITE)
                .addRow("Electric", "Tesla", BLUES)
                .build();
        Assert.assertEquals("Zoom-zoom", decisionTable.accept("CX5", SIMPLE_WHITE));
        Assert.assertEquals(FALLBACK, decisionTable.accept("CX5", GREEN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionIfNoneLeafSupplied() {
        DecisionTable<String> decisionTable = DecisionTable.of(FALLBACK, CARS, colors)
                .addRow("Cool ones", "Japan", DARK_RED)
                .addRow("Zoom-zoom", "CX5", GRAY_SCALE)
                .addRow("Electric", "Tesla", BLUES)
                .build();
        decisionTable.accept("CX5", GRAY_SCALE);
    }
}

