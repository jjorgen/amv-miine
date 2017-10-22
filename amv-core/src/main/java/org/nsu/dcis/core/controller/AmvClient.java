package org.nsu.dcis.core.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.apache.log4j.Logger;


@Controller
public class AmvClient {
    public static final String ASPECT_MINING_USE_CASE = "AspectMining";

    private static AmvClient amvClient = new AmvClient();

    private Logger log = Logger.getLogger(getClass().getName());

    public static void main(String[] args) {
        amvClient.run(args);
    }

    public void run(String[] args) {
        listParametersPassedWhenStartingApplication(args);
        dispatch(args);
        if (isValidArgumentsPassedForUseCase(args)) {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            AmvClientController amvClientController = getAmvClientController(context);
            amvClientController.start(args);
        } else {
            terminateApplicationAbnormally(args);
        }
    }

    private void dispatch(String[] args) {
    }

    private AmvClientController getAmvClientController(ApplicationContext context) {
        return (AmvClientController) context.getBean("amvClientController");
    }

    private void terminateApplicationAbnormally(String[] args) {
        throw new IllegalArgumentException("Illegal arguments passed to AmvClient application, arguments: " + args);
    }

    private boolean isValidArgumentsPassedForUseCase(String[] args) {
        return ("AspectMining".equalsIgnoreCase(ASPECT_MINING_USE_CASE)) ? true : false;
    }

    private void listParametersPassedWhenStartingApplication(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(" ")
                    .append(arg);
        }
        log.info("Arguments passed when starting the application: " + sb.toString());
    }
}