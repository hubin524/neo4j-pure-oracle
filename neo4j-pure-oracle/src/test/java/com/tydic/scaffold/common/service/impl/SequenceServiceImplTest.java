package com.tydic.scaffold.common.service.impl;

import com.tydic.scaffold.common.service.SequenceService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jianghs
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-02-25 10:33
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SequenceServiceImplTest {

    @Autowired
    private SequenceService sequenceService;

    @ParameterizedTest
    @MethodSource("typeProvider")
    public void dailyNext(String type) {
        IntStream.range(1,30).forEach(item -> {
            System.out.println("item------------"+sequenceService.dailyNext(type));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    Stream<String> typeProvider(){
        return Stream.of("ORDER", "PROJECT");
    }
}