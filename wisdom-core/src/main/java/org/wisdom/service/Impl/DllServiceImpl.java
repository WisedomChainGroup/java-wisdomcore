package org.wisdom.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.wisdom.ApiResult.APIResult;
import org.wisdom.command.Dllparameter;
import org.wisdom.service.DllService;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Service
public class DllServiceImpl implements DllService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Object CallMethod(Dllparameter dllparameter) {
        Class[] classes = new Class[0];
        Object[] newobject = new Class[0];
        if (dllparameter.getClasstype() != null || dllparameter.getObjectList() != null) {
            if (dllparameter.getClasstype().size() != dllparameter.getObjectList().size()) {
                return APIResult.newFailResult(5000, "The parameter is inconsistent");
            }
            List<Integer> integerList = dllparameter.getClasstype();
            List<Object> objectss = dllparameter.getObjectList();
            classes = new Class[integerList.size()];
            newobject = new Object[integerList.size()];
            int index = 0;
            for (Integer integer : integerList) {
                if (integer == 0) {//DataSource
                    classes[index] = DataSource.class;
                    DataSource dataSource = jdbcTemplate.getDataSource();
                    newobject[index] = dataSource;
                }
                if (integer == 1) {//string
                    classes[index] = String.class;
                    newobject[index] = objectss.get(index);
                } else if (integer == 2) {//int
                    classes[index] = int.class;
                    newobject[index] = objectss.get(index);
                } else if (integer == 3) {//boolean
                    classes[index] = boolean.class;
                    newobject[index] = objectss.get(index);
                } else if (integer == 4) {//double
                    classes[index] = double.class;
                    newobject[index] = objectss.get(index);
                } else if (integer == 5) {//long
                    classes[index] = long.class;
                    newobject[index] = objectss.get(index);
                }
                index++;
            }
        }
        String softPath = "file:" + System.getProperty("user.dir") + File.separator + "libs" + File.separator + dllparameter.getJarname();
        if (softPath.contains("//")) {
            softPath = softPath.replace("//", "/");
        }
        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL(softPath)}, Thread.currentThread().getContextClassLoader());
            Class demo = classLoader.loadClass(dllparameter.getClasspackage());
            Object object = demo.newInstance();
            return APIResult.newFailResult(2000, "SUCCESS", demo.getMethod(dllparameter.getMethodname(), classes).invoke(object, newobject));
        } catch (MalformedURLException e) {
            return APIResult.newFailResult(5000, "URL format errorr");
        } catch (ClassNotFoundException e) {
            return APIResult.newFailResult(5000, "Class cannot be found");
        } catch (IllegalAccessException e) {
            return APIResult.newFailResult(5000, "Error instantiating the called method");
        } catch (InstantiationException e) {
            return APIResult.newFailResult(5000, "Error instantiating the called method");
        } catch (NoSuchMethodException e) {
            return APIResult.newFailResult(5000, "Method cannot be found");
        } catch (InvocationTargetException e) {
            return APIResult.newFailResult(5000, "There was an error in the method called");
        }
    }
}
