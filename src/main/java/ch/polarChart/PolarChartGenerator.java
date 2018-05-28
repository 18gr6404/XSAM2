/*
 * Copyright (c) 2018 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.polarChart;

import ch.model.OverviewParameters;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PolarChartGenerator {
    private XYSeries<XYChartItem> xySeries1;
    private XYSeries<XYChartItem> xySeries2;
    private XYSeries<XYChartItem> xySeries3;
    private XYSeries<XYChartItem> xySeries4;
    private XYSeries<XYChartItem> xySeries5;
    private XYSeries<XYChartItem> xySeries6;
    private XYSeries<XYChartItem> xySeries7;

    private PolarChart<XYChartItem> polarChart;


    public StackPane generateChart(OverviewParameters overViewParam) {


        List<XYChartItem> xyItems1 = new ArrayList<>();
        List<XYChartItem> xyItems2 = new ArrayList<>();
        List<XYChartItem> xyItems3 = new ArrayList<>();
        List<XYChartItem> xyItems4 = new ArrayList<>();
        List<XYChartItem> xyItems5 = new ArrayList<>();
        List<XYChartItem> xyItems6 = new ArrayList<>();
        List<XYChartItem> xyItems7 = new ArrayList<>();

        double xy1Length;
        if (overViewParam.getAvgRelivMed() < 5) {
            xy1Length = 20 * overViewParam.getAvgRelivMed();
        }else {xy1Length = 100;}
        xyItems1.add(new XYChartItem(0, 0));
        xyItems1.add(new XYChartItem(46, xy1Length));
        xyItems1.add(new XYChartItem(50, xy1Length));
        xyItems1.add(new XYChartItem(55, xy1Length));
        xyItems1.add(new XYChartItem(60, xy1Length));
        xyItems1.add(new XYChartItem(65, xy1Length));
        xyItems1.add(new XYChartItem(70, xy1Length));
        xyItems1.add(new XYChartItem(75, xy1Length));
        xyItems1.add(new XYChartItem(80, xy1Length));
        xyItems1.add(new XYChartItem(85, xy1Length));
        xyItems1.add(new XYChartItem(89, xy1Length));
        xyItems1.add(new XYChartItem(90, 0));

        double xy2Length;
        if (overViewParam.getAvgActiveLim() < 5) {
            xy2Length = 20 * overViewParam.getAvgActiveLim();
        }else {xy2Length = 100;}
        xyItems2.add(new XYChartItem(0, 0));
        xyItems2.add(new XYChartItem(1, xy2Length));
        xyItems2.add(new XYChartItem(5, xy2Length));
        xyItems2.add(new XYChartItem(10, xy2Length));
        xyItems2.add(new XYChartItem(15, xy2Length));
        xyItems2.add(new XYChartItem(20, xy2Length));
        xyItems2.add(new XYChartItem(25, xy2Length));
        xyItems2.add(new XYChartItem(30, xy2Length));
        xyItems2.add(new XYChartItem(35, xy2Length));
        xyItems2.add(new XYChartItem(40, xy2Length));
        xyItems2.add(new XYChartItem(44, xy2Length));

        double xy3Length;
        if (overViewParam.getAvgNightSymptoms() < 5) {
            xy3Length = 20 * overViewParam.getAvgNightSymptoms();
        }else {xy3Length = 100;}
        xyItems3.add(new XYChartItem(0, 0));
        xyItems3.add(new XYChartItem(316, xy3Length));
        xyItems3.add(new XYChartItem(320, xy3Length));
        xyItems3.add(new XYChartItem(325, xy3Length));
        xyItems3.add(new XYChartItem(330, xy3Length));
        xyItems3.add(new XYChartItem(335, xy3Length));
        xyItems3.add(new XYChartItem(340, xy3Length));
        xyItems3.add(new XYChartItem(345, xy3Length));
        xyItems3.add(new XYChartItem(350, xy3Length));
        xyItems3.add(new XYChartItem(355, xy3Length));
        xyItems3.add(new XYChartItem(359, xy3Length));

        double xy4Length;
        if (overViewParam.getAvgDaySymptoms() < 5) {
            xy4Length = 20 * overViewParam.getAvgDaySymptoms();
        }else {xy4Length = 100;}
        xyItems4.add(new XYChartItem(0, 0));
        xyItems4.add(new XYChartItem(271, xy4Length));
        xyItems4.add(new XYChartItem(275, xy4Length));
        xyItems4.add(new XYChartItem(280, xy4Length));
        xyItems4.add(new XYChartItem(285, xy4Length));
        xyItems4.add(new XYChartItem(290, xy4Length));
        xyItems4.add(new XYChartItem(295, xy4Length));
        xyItems4.add(new XYChartItem(300, xy4Length));
        xyItems4.add(new XYChartItem(305, xy4Length));
        xyItems4.add(new XYChartItem(310, xy4Length));
        xyItems4.add(new XYChartItem(314, xy4Length));

        double xy5Length;
        xy5Length = overViewParam.getAvgFEV1();
        xyItems5.add(new XYChartItem(0, 0));
        xyItems5.add(new XYChartItem(91, xy5Length));
        xyItems5.add(new XYChartItem(95, xy5Length));
        xyItems5.add(new XYChartItem(100, xy5Length));
        xyItems5.add(new XYChartItem(105, xy5Length));
        xyItems5.add(new XYChartItem(110, xy5Length));
        xyItems5.add(new XYChartItem(115, xy5Length));
        xyItems5.add(new XYChartItem(120, xy5Length));
        xyItems5.add(new XYChartItem(125, xy5Length));
        xyItems5.add(new XYChartItem(130, xy5Length));
        xyItems5.add(new XYChartItem(135, xy5Length));
        xyItems5.add(new XYChartItem(140, xy5Length));
        xyItems5.add(new XYChartItem(145, xy5Length));
        xyItems5.add(new XYChartItem(150, xy5Length));
        xyItems5.add(new XYChartItem(155, xy5Length));
        xyItems5.add(new XYChartItem(160, xy5Length));
        xyItems5.add(new XYChartItem(165, xy5Length));
        xyItems5.add(new XYChartItem(170, xy5Length));
        xyItems5.add(new XYChartItem(175, xy5Length));
        xyItems5.add(new XYChartItem(179, xy5Length));

        double xy6Length = overViewParam.getAvgEveningPEF();
        xyItems6.add(new XYChartItem(0, 0));
        xyItems6.add(new XYChartItem(181, xy6Length));
        xyItems6.add(new XYChartItem(185, xy6Length));
        xyItems6.add(new XYChartItem(190, xy6Length));
        xyItems6.add(new XYChartItem(195, xy6Length));
        xyItems6.add(new XYChartItem(200, xy6Length));
        xyItems6.add(new XYChartItem(205, xy6Length));
        xyItems6.add(new XYChartItem(210, xy6Length));
        xyItems6.add(new XYChartItem(215, xy6Length));
        xyItems6.add(new XYChartItem(220, xy6Length));
        xyItems6.add(new XYChartItem(224, xy6Length));


        double xy7Length = overViewParam.getAvgMorningPEF();
        xyItems7.add(new XYChartItem(0, 0));
        xyItems7.add(new XYChartItem(226, xy7Length));
        xyItems7.add(new XYChartItem(230, xy7Length));
        xyItems7.add(new XYChartItem(235, xy7Length));
        xyItems7.add(new XYChartItem(240, xy7Length));
        xyItems7.add(new XYChartItem(245, xy7Length));
        xyItems7.add(new XYChartItem(250, xy7Length));
        xyItems7.add(new XYChartItem(255, xy7Length));
        xyItems7.add(new XYChartItem(260, xy7Length));
        xyItems7.add(new XYChartItem(265, xy7Length));
        xyItems7.add(new XYChartItem(269, xy7Length));
       /* int Ypropery = 80;
        xyItems1.add(new XYChartItem(0, 0));
        xyItems1.add(new XYChartItem(45, 0));
        xyItems1.add(new XYChartItem(1.0, Ypropery));
        xyItems1.add(new XYChartItem(44, Ypropery));
        for (int i = 1; i<9; i++) {
            xyItems1.add(new XYChartItem(i * 5, Ypropery));
        }*/


        Helper.orderXYChartItemsByX(xyItems1, Order.ASCENDING);

        xySeries1 = new XYSeries(xyItems1, ChartType.POLAR, Color.rgb(255, 0, 0, 0.5), Color.RED);
        xySeries1.setStroke(Color.rgb(90, 90, 90));
        xySeries1.setSymbolsVisible(false);
        xySeries2 = new XYSeries(xyItems2, ChartType.POLAR, Color.rgb(0, 255, 0, 0.5), Color.RED);
        xySeries2.setStroke(Color.rgb(90, 90, 90));
        xySeries2.setSymbolsVisible(false);
        xySeries3 = new XYSeries(xyItems3, ChartType.POLAR, Color.rgb(0, 0, 255, 0.5), Color.RED);
        xySeries3.setStroke(Color.rgb(90, 90, 90));
        xySeries3.setSymbolsVisible(false);

        xySeries4 = new XYSeries(xyItems4, ChartType.POLAR, Color.rgb(140, 255, 140, 0.5), Color.RED);
        xySeries4.setStroke(Color.rgb(90, 90, 90));
        xySeries4.setSymbolsVisible(false);

        xySeries5 = new XYSeries(xyItems5, ChartType.POLAR, Color.rgb(220, 100, 10, 0.5), Color.RED);
        xySeries5.setStroke(Color.rgb(90, 90, 90));
        xySeries5.setSymbolsVisible(false);

        xySeries6 = new XYSeries(xyItems6, ChartType.POLAR, Color.rgb(140, 10, 140, 0.5), Color.RED);
        xySeries6.setStroke(Color.rgb(90, 90, 90));
        xySeries6.setSymbolsVisible(false);

        xySeries7 = new XYSeries(xyItems7, ChartType.POLAR, Color.rgb(0, 255, 255, 0.5), Color.RED);
        xySeries7.setStroke(Color.rgb(90, 90, 90));
        xySeries7.setSymbolsVisible(false);



        XYPane polarPane = new XYPane(xySeries1, xySeries2, xySeries3, xySeries4, xySeries5, xySeries6, xySeries7);


        polarPane.setLowerBoundY(polarPane.getDataMinY() + 28);
        polarPane.getChartBackground();
        polarPane.setUpperBoundY(100);



        polarChart = new PolarChart<>(polarPane);

        StackPane pane = new StackPane(polarChart);
        return pane;
    }






/*    @Override public void start(Stage stage) {

        //pane.setPadding(new Insets(10));

        Scene scene = new Scene(new StackPane(pane));

        stage.setTitle("Polar Chart");
        stage.setScene(scene);
        stage.show();

//        timer.start();
    }*/
}
