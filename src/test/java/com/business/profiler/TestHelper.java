package com.business.profiler;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.business.changeRequest;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Component
public class TestHelper {
    public ServiceRequest<Product> createProductRequest(String fileName) throws Exception{
        File file = new File(this.getClass().getResource("/" + fileName).getFile());
        String inProduct = readFile(file);

        TypeReference<ServiceRequest<Product>> ref = new TypeReference<ServiceRequest<Product>>() {};
        ServiceRequest<Product> in = ObjectMapperFactory.getMapper().readValue(inProduct,ref);

        return in;
    }

    public ServiceRequest<changeRequest> createChangeRequest(String fileName) throws Exception{
        File file = new File(this.getClass().getResource("/" + fileName).getFile());
        String inProduct = readFile(file);

        TypeReference<ServiceRequest<changeRequest>> ref = new TypeReference<ServiceRequest<changeRequest>>() {};
        ServiceRequest<changeRequest> in = ObjectMapperFactory.getMapper().readValue(inProduct,ref);

        return in;
    }

    public String readFile(File file) throws Exception{
        if(null!=file){
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine())!=null){
                sb.append(line);

            }
            return sb.toString();

        }
        return null;
    }
}
