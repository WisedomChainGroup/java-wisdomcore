package org.ethereum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Configuration
public class TestBean {

    private static final Logger logger = LoggerFactory.getLogger("facade");

    public interface Car {

        void start();
        void stop();
    }

    @Component
    public class Benz implements Car {

        public Benz() {

            logger.info("Benz construct...");
        }

        public void start() {

            logger.info("Benz startListening...");
        }

        public void stop() {

            logger.info("Benz stop...");
        }
    }


    public static void main(String[] args) throws Exception {

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(TestBean.class);
        context.registerShutdownHook();
        Car benz = context.getBean(Car.class);
        benz.start();
    }
}
