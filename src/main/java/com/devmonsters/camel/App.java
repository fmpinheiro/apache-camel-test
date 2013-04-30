package com.devmonsters.camel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.main.Main;

public class App {

    public static void main(final String args[]) throws Exception {
        final Main main = new Main();
        main.addRouteBuilder(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                this.from("file:/tmp/teste?delay=5000").
                    log("Recebi arquivo ${file:name}").
                    choice().
//                        when(header("${file:onlyname.noext}").isNotEqualTo("bar")).
//                            to("file:/tmp/processado/bar").
                        when(simple("${file:onlyname.noext} contains 'fernando'")).
                            process(new ProcessadorTeste()).
                            to("file:/tmp/processado/fernando").
                        when(simple("${file:onlyname.noext} contains 'diego'")).
                            to("file:/tmp/processado/diego");
            }
        });
        main.enableHangupSupport();
        main.run();
    }
}

class ProcessadorTeste implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        System.err.println("***");

        try (final InputStream is = new ByteArrayInputStream(exchange.getIn().getBody(byte[].class))) {
            try (Scanner scanner = new Scanner(is)) {
                while (scanner.hasNextLine()) {
                    System.err.println(scanner.nextLine());
                }
            }
        }
        exchange.getOut().setHeader(FileComponent.FILE_EXCHANGE_FILE    , System.currentTimeMillis());

        System.err.println("***");
    }
}