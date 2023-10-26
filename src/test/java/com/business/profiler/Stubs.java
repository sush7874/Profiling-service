package com.business.profiler;

import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Stubs {

    private String host = null;
    private int port = 8070;

    public Stubs(String host, int port){
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        String host = "localhost";
        int port = 8070;
        Stubs util = new Stubs(host, port);

        util.registerQBooksSuccessResponse();
        util.registerQBPaymentsResponse();
        util.registerQBPayrollSuccessResponse();
        util.registerTSheetsSuccessResponse();
        util.registerRequestSubmissionSuccessResponse();

    }

    private void registerQBooksSuccessResponse() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseFromQBooks.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service/QBooks")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes))
        ));
    }

    private void registerQBPayrollSuccessResponse() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseFromQBPayroll.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service/QBPayroll")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes))
        ));
    }

    private void registerQBPaymentsResponse() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseFromQBPayments.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service/QBPayments")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes))
        ));
    }

    private void registerTSheetsSuccessResponse() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseFromTsheets.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service/TSheets")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes))
        ));
    }

    private void registerRequestSubmissionSuccessResponse() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseForRequestSubmission.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes))
        ));
    }

    private void registerRequestSubmissionSuccessResponseWithDelay() throws URISyntaxException, IOException {
        configureFor(this.host, this.port);
        Path path = Paths.get(getClass().getClassLoader().getResource("SuccessResponseForRequestSubmission.json").toURI());
        byte[] fileBytes = Files.readAllBytes(path);

        stubFor(post(urlEqualTo("/service")).willReturn(
                aResponse().withStatus(200)
                        .withBody(new String(fileBytes)).withFixedDelay(10000)
        ));
    }

}
