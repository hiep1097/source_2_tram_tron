package com.dagwo.main;

import com.dagwo.algorithm.DA_GWO.DA_GWO;
import com.dagwo.chart.ParetoChart;
import com.dagwo.problem.f_RMC_CWT;
import com.dagwo.problem.f_RMC_TWC;
import com.dagwo.problem.f_SimRMC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.ArrayList;

import static com.dagwo.main.Util.checkFixPareto;
import static com.dagwo.main.Util.writeParetoDataToFile;

public class Main_DA_GWO {

    public static void main(String[] args) throws IOException {

        f_RMC_CWT ff_cwt = new f_RMC_CWT();
        f_RMC_CWT ff_twc = new f_RMC_TWC();

        int maxiter = 50;
        int N = 30;

        DA_GWO cwt = new DA_GWO(ff_cwt, ff_cwt.Lower, ff_cwt.Upper, maxiter, N);
        DA_GWO twc = new DA_GWO(ff_twc, ff_twc.Lower, ff_twc.Upper, maxiter, N);

        long startTime = System.currentTimeMillis();

        cwt.execute();
        twc.execute();

        int lengthOfItem = maxiter * 2;
        int numberOfTruckWanted = ff_cwt.Upper.length;

        double[][] data = new double[lengthOfItem][numberOfTruckWanted];

        double[][] temp_cwt = cwt.getArrayRandomResult();
        for (int i = 0; i < maxiter; i++) {
            data[i] = temp_cwt[i];
        }

        double[][] temp_twc = twc.getArrayRandomResult();
        for (int i = 0; i < maxiter; i++) {
            data[maxiter + i] = temp_twc[i];
        }

        ObservableList<XYChart.Data<Number, Number>> lstData = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> lstParetoData = FXCollections.observableArrayList();

        ArrayList<f_SimRMC> lst_fSimRMC = new ArrayList<f_SimRMC>();

        for (int i = 0; i < lengthOfItem; i++) {
            f_SimRMC fSimRMC = new f_SimRMC();
            fSimRMC.Execute(data[i]);
            lst_fSimRMC.add(fSimRMC);

            XYChart.Data<Number, Number> _data = new XYChart.Data<>();

            _data.setXValue(fSimRMC.CWT);
            _data.setYValue(fSimRMC.TWC);

            lstData.add(_data);

            XYChart.Data<Number, Number> _pareto_data = new XYChart.Data<>();
            _pareto_data.setXValue(fSimRMC.CWT);
            _pareto_data.setYValue(fSimRMC.TWC);

            lstParetoData.add((_pareto_data));
        }

        f_SimRMC _fSimRMC_CWT = new f_SimRMC();
        f_SimRMC _fSimRMC_TWC = new f_SimRMC();

        _fSimRMC_CWT.Execute(cwt.getBestArray());
        _fSimRMC_TWC.Execute(twc.getBestArray());

        System.out.println("----->> Best of CWT <<-----");
        System.out.println("CWT = " + _fSimRMC_CWT.CWT + " - TWC = " + _fSimRMC_CWT.TWC);
        cwt.toStringNew("Optimized value CWT = ");

        System.out.println("----->> Best of TWC <<-----");
        System.out.println("CWT = " + _fSimRMC_TWC.CWT + " - TWC = " + _fSimRMC_TWC.TWC);
        twc.toStringNew("Optimized value TWC = ");

        do {
            for (int i = 0; i < lstParetoData.size() - 1; i++) {
                float cwt_value_i = (float) lstParetoData.get(i).getXValue();
                float twc_value_i = (float) lstParetoData.get(i).getYValue();

                for (int j = i + 1; j < lstParetoData.size(); j++) {
                    float cwt_value_j = (float) lstParetoData.get(j).getXValue();
                    float twc_value_j = (float) lstParetoData.get(j).getYValue();

                    if ((cwt_value_i < cwt_value_j && twc_value_i < twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i < cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i < twc_value_j)) {
                        lstParetoData.remove(j);
                        lst_fSimRMC.remove(j);
                        j--;
                        break;
                    }
                }
            }

            for (int i = lstParetoData.size() - 1; i >= 1; i--) {
                float cwt_value_i = (float) lstParetoData.get(i).getXValue();
                float twc_value_i = (float) lstParetoData.get(i).getYValue();

                for (int j = i - 1; j >= 0; j--) {
                    float cwt_value_j = (float) lstParetoData.get(j).getXValue();
                    float twc_value_j = (float) lstParetoData.get(j).getYValue();

                    if ((cwt_value_i < cwt_value_j && twc_value_i < twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i < cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i < twc_value_j)) {
                        lstParetoData.remove(j);
                        lst_fSimRMC.remove(j);
                        j++;
                        break;
                    }
                }
            }
        } while(checkFixPareto(lstParetoData) == false);

//        for(int k = 0; k < 5; k++) {
//            int max = 0;
//            float cwt_max = (float) lstData.get(max).getXValue();
//            float twc_max = (float) lstData.get(max).getYValue();
//
//            for (int i = 0; i < lstData.size(); i++) {
//                float cwt_value_i = (float) lstData.get(i).getXValue();
//                float twc_value_i = (float) lstData.get(i).getYValue();
//
//                if (cwt_value_i > cwt_max && twc_value_i > twc_max) {
//                    max = i;
//                    cwt_max = cwt_value_i;
//                    twc_max = twc_value_i;
//                }
//            }
//
//            lstData.remove(max);
//        }

        System.out.println("--> Complete Check fix Pareto data");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println((totalTime / 1000.0) + " sec");

        System.out.println("=============== RESULT ===============");
        System.out.println("Result have " + lstParetoData.size() + " values");

        for(int i = 0; i < lst_fSimRMC.size(); i++) {
            System.out.println("--> No. " + (i + 1) + " <--" );
            lst_fSimRMC.get(i).PrintRMC();
            lst_fSimRMC.get(i).PrintPlanOfTruck();
            lst_fSimRMC.get(i).printSimulatedResult();
        }

        //ghi so lieu vao file de ve bieu do
        writeParetoDataToFile("DA_GWO", lstData, lstParetoData);

        ParetoChart chart = new ParetoChart();
        ParetoChart.algorithmName = "DA_GWO";
        ParetoChart.lstData = lstData;
        ParetoChart.lstParetoData = lstParetoData;

        chart.main(args);
    }
}
