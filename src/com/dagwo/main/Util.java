package com.dagwo.main;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static String folderParetoData = "pareto_data";

    public static void writeParetoDataToFile(String algorithmName,
                                             ObservableList<XYChart.Data<Number, Number>> lstData,
                                             ObservableList<XYChart.Data<Number, Number>> lstParetoData) throws IOException {
        String dataFileName = folderParetoData + "/" + algorithmName + "_data.txt";
        String pareToDataFileName = folderParetoData + "/" + algorithmName + "_pareto_data.txt";

        File fileData = new File(dataFileName);
        File fileParetoData = new File(pareToDataFileName);
        OutputStream outputStream = new FileOutputStream(fileData);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

        for (XYChart.Data<Number, Number> item: lstData){
            String data = item.getXValue()+"|"+item.getYValue();
            outputStreamWriter.write(data);
            outputStreamWriter.write("\n");
        }
        outputStreamWriter.flush();

        outputStream = new FileOutputStream(fileParetoData);
        outputStreamWriter = new OutputStreamWriter(outputStream);

        for (XYChart.Data<Number, Number> item: lstParetoData){
            String data = item.getXValue()+"|"+item.getYValue();
            outputStreamWriter.write(data);
            outputStreamWriter.write("\n");
        }
        outputStreamWriter.flush();
    }

    public static String[] readParetoDataFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        List<String> data = new ArrayList<>();

        String line = "";
        while((line = reader.readLine()) != null){
            data.add(line);
        }
        return (String[]) data.toArray();
    }

    public static boolean checkFixPareto(ObservableList<XYChart.Data<Number, Number>> lstParetoData){
        System.out.println("Check fix Pareto data");
        for(int i = 0; i < lstParetoData.size() - 1; i++)
        {
            float cwt_value_i = (float)lstParetoData.get(i).getXValue();
            float twc_value_i = (float)lstParetoData.get(i).getYValue();

            for(int j = i + 1; j < lstParetoData.size(); j++)
            {
                float cwt_value_j = (float)lstParetoData.get(j).getXValue();
                float twc_value_j = (float)lstParetoData.get(j).getYValue();

                if ((cwt_value_i < cwt_value_j && twc_value_i < twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i < cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i < twc_value_j)) {
                    return false;
                }
            }
        }
        return true;
    }
}
