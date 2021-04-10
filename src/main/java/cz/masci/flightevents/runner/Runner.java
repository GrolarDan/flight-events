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
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
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
        ProfileEvents profileEvents = root.getProfileEvents();
        
        profileEvents.getVoiceMessageEvents().forEach(voiceMessage -> log.debug(voiceMessage.toString()));
        profileEvents.getConditionEvents().forEach(condition -> log.debug(condition.toString()));
        profileEvents.getMotionEvents().forEach(motion -> log.debug(motion.toString()));
//        var profileEvents = unmarshall(inputFileName);
//        profileEvents.getVoiceMessageEvents().forEach(voiceMessage -> log.debug(voiceMessage.toString()));
    }

    public FakeRoot unmarshall(String filename) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(FakeRoot.class);
        return (FakeRoot) context.createUnmarshaller()
                .unmarshal(new FileReader(filename));
    }
    
//    public ProfileEvents unmarshall(String fileName) throws XMLStreamException, JAXBException {
//        XMLInputFactory xif = XMLInputFactory.newFactory();
//        StreamSource xml = new StreamSource(fileName);
//        XMLStreamReader xsr = xif.createXMLStreamReader(xml);
//        xsr.nextTag();
//        while(!xsr.getLocalName().equals("ProfileEvents")) {
//            xsr.nextTag();
//        }
//
//        JAXBContext jc = JAXBContext.newInstance(ProfileEvents.class);
//        Unmarshaller unmarshaller = jc.createUnmarshaller();
//        JAXBElement<ProfileEvents> jb = unmarshaller.unmarshal(xsr, ProfileEvents.class);
//        xsr.close();
//
//        return jb.getValue();
//    }
}
