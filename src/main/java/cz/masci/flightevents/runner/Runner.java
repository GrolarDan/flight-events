/*
 * Copyright (C) 2021 Daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.masci.flightevents.runner;

import cz.masci.flightevents.model.FakeRoot;
import cz.masci.flightevents.model.ProfileEvents;
import cz.masci.flightevents.model.dto.EventDTO;
import cz.masci.flightevents.services.EventMapper;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Main application runner.
 *
 * @author Daniel Masek
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Runner implements ApplicationRunner {

    private final EventMapper eventMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Running Flight events XML Parser.");
        String inputFileName = Optional.ofNullable(args.getOptionValues("input")).orElseThrow().get(0);
        String outputFileName = Optional.ofNullable(args.getOptionValues("output")).orElse(List.of("output.csv")).get(0);

        log.info("Parsing file: {}", inputFileName);

        List<EventDTO> events = getEvents(inputFileName);

        printEventsToConsole(events);
        printEventsToCsv(events, outputFileName);
    }

    private List<EventDTO> getEvents(String fileName) throws JAXBException, IOException {
        var root = unmarshall(fileName);
        ProfileEvents profileEvents = root.getProfileEvents();

        return profileEvents.getEvents().stream()
                .peek(event -> log.debug(event.toString()))
                .map(eventMapper::map)
                .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .toList();
    }

    private FakeRoot unmarshall(String fileName) throws JAXBException, IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        fileInputStream.skip("<?xml version=\"1.0\"?>".length());
        
        List<InputStream> streams = Arrays.asList(
                new ByteArrayInputStream("<root>".getBytes()),
                new BufferedInputStream(fileInputStream),
                new ByteArrayInputStream("</root>".getBytes())
        );
        InputStream is = new SequenceInputStream(Collections.enumeration(streams));
        
        JAXBContext context = JAXBContext.newInstance(FakeRoot.class);
        return (FakeRoot) context.createUnmarshaller()
                .unmarshal(new InputStreamReader(is));
    }

    private void printEventsToConsole(List<EventDTO> events) {
        System.out.println("\nstartTime; duration; TYPE; message");

        events.stream().forEach(event -> System.out.println(mapEventToString(event)));
    }

    private void printEventsToCsv(List<EventDTO> events, String fileName) throws IOException {
        log.info("Exporting to file {}", fileName);
        
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            out.println("startTime; duration; TYPE; message;");
            
            events.stream().forEach(event -> out.println(mapEventToString(event)));
            
            out.flush();
        }
    }
    
    private String mapEventToString(EventDTO event) {
        return String.format("%2.2f; %2.2f; %s; %s;",
                event.getStartTime(), event.getDuration(), event.getType().getText(), event.getMessage()
        );
    }

}
