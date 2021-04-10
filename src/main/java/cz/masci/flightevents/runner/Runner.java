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
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
public class Runner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Running Flight events XML Parser.");
        String inputFileName = Optional.of(args.getOptionValues("input").get(0)).orElseThrow();
        
        log.info("Parsing file: {}", inputFileName);
        
        var root = unmarshall(inputFileName);
        root.getProfileEvents().getVoiceMessageEvents().forEach(voiceMessage -> log.debug(voiceMessage.toString()));
    }

    public FakeRoot unmarshall(String filename) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(FakeRoot.class);
        return (FakeRoot) context.createUnmarshaller()
                .unmarshal(new FileReader(filename));
    }
}
