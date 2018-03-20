/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author sznicci
 */
public class JourneyDataCsvFileRead {

    static final String CSV_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\Tasks\\000TaskFiles\\csvFile\\JourneyDataExtract01Jan1503Jan15.csv";

    public static void main(String[] args) throws IOException {
        try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(CSV_FILENAME), CsvPreference.STANDARD_PREFERENCE)) {
            // Define headers for csv file because they do not match with JourneyDataBean fields
            final String[] headers = new String[]{"rentalId", "duration", "bikeId",
                "endDate", "endStationId", "endStationName", "startDate", "startStationId", "startStationName"};
            final CellProcessor[] processors = getProcessors();

            JourneyDataBean journeyData;
            UsageBean usage = new UsageBean();

            DBConnection connectUsage = new DBConnection();
            String SQL;

            long endTime, startTime;
            int i = 0;
            while ((journeyData = beanReader.read(JourneyDataBean.class, headers, processors)) != null && i < 500) {
                // Set fields for usage
                usage.setDate(new java.sql.Date(journeyData.getStartDate().getTime()));
                endTime = journeyData.getEndDate().getTime();
                usage.setEndTime(new Time(endTime));
                usage.setEndStationId(journeyData.getEndStationId());
                startTime = journeyData.getStartDate().getTime();
                usage.setStartTime(new Time(startTime));
                usage.setStartStationId(journeyData.getStartStationId());

                // Create SQL statement
                SQL = insertStatementForUsageTable(usage);

                // Insert into database
                connectUsage.insertIntoUsage(SQL);

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create SQL insert statement
     */
    private static String insertStatementForUsageTable(UsageBean usage) {
        String SQL = "INSERT INTO public.usage(\n"
                + "id, date, start_time, end_time, start_station_id, end_station_id)\n"
                + "VALUES (default, '" + usage.getDate() + "', '" + usage.getStartTime() + "', '" + usage.getEndTime() + "', '" + usage.getStartStationId()
                + "', " + usage.getEndStationId() + ");";

        return SQL;
    }

    /**
     * Sets up the processors.
     */
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{
            new NotNull(new ParseInt()), // rentalId
            new NotNull(new ParseInt()), // duration
            new NotNull(new ParseInt()), // bikeId
            new NotNull(new ParseDate("dd/MM/yyyy mm:ss")), // endDate
            new NotNull(new ParseInt()), // endStationId
            new NotNull(), // endStationName
            new NotNull(new ParseDate("dd/MM/yyyy mm:ss")), // startDate
            new NotNull(new ParseInt()), // startStationId
            new NotNull(), // startStationName
        };
        return processors;
    }
}
