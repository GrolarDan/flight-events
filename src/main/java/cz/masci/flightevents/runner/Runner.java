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
import cz.masci.flightevents.model.events.BaseEvent;
import cz.masci.flightevents.services.EventMapper;
import java.io.FileReader;
import java.io.IOException;
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
        String inputFileName = Optional.of(args.getOptionValues("input").get(0)).orElseThrow();

        log.info("Parsing file: {}", inputFileName);

        var root = unmarshall(inputFileName);
        ProfileEvents profileEvents = root.getProfileEvents();

        profileEvents.getEvents().forEach(event -> log.debug(event.toString()));

        List<EventDTO> events = mapEvents(profileEvents.getEvents());

        printEvents(events);
    }

    private FakeRoot unmarshall(String filename) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(FakeRoot.class);
        return (FakeRoot) context.createUnmarshaller()
                .unmarshal(new FileReader(filename));
    }

    private List<EventDTO> mapEvents(List<BaseEvent> events) {
        return events.stream()
                .map(eventMapper::map)
                .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .toList();
    }

    private void printEvents(List<EventDTO> events) {
        System.out.println("\nstartTime; duration; TYPE; message");

        events.stream().forEach(this::printEvent);
    }

    private void printEvent(EventDTO event) {
        System.out.println(
                String.format("%2.2f; %2.2f; %s; %s",
                        event.getStartTime(), event.getDuration(), event.getType().getText(), event.getMessage()
                )
        );
    }

}
