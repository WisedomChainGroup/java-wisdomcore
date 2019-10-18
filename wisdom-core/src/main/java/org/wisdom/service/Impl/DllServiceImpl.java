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
        Class[] classes=null;
        Object[] newobject=null;
        DataSource dataSource=jdbcTemplate.getDataSource();
        if(dllparameter.getClasstype()==null || dllparameter.getObjectList()==null){
            classes=new Class[1];
            classes[0]=DataSource.class;
            newobject=new Object[1];
            newobject[0]=dataSource;
        }else{
            List<Integer> integerList=dllparameter.getClasstype();
            classes=new Class[integerList.size()+1];
            classes[0]=DataSource.class;
            int x=1;
            for(Integer integer:integerList){
                if(integer==1){//string
                    classes[x]=String.class;
                }else if(integer==2){//int
                    classes[x]=int.class;
                }else if(integer==3){//boolean
                    classes[x]=boolean.class;
                }else if(integer==4){//double
                    classes[x]=double.class;
                }else if(integer==5){//long
                    classes[x]=long.class;
                }
                x++;
            }
            x=1;
            List<Object> objectss=dllparameter.getObjectList();
            newobject=new Object[objectss.size()+1];
            newobject[0]=dataSource;
            for(Object object:objectss){
                newobject[x]=object;
                x++;
            }
        }
        String softPath="file:"+System.getProperty("user.dir") + File.separator +"libs"+ File.separator +dllparameter.getJarname();
        if(softPath.contains("//")){
            softPath=softPath.replace("//","/");
        }
        try{
            URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL(softPath)},Thread.currentThread().getContextClassLoader());
            Class demo = classLoader.loadClass(dllparameter.getClasspackage());
            Object object = demo.newInstance();
            return APIResult.newFailResult(2000,"SUCCESS",demo.getMethod(dllparameter.getMethodname(), classes).invoke(object,newobject));
        } catch (MalformedURLException e) {
            return APIResult.newFailResult(5000,"URL format errorr");
        } catch (ClassNotFoundException e) {
            return APIResult.newFailResult(5000,"Class cannot be found");
        } catch (IllegalAccessException e) {
            return APIResult.newFailResult(5000,"Error instantiating the called method");
        } catch (InstantiationException e) {
            return APIResult.newFailResult(5000,"Error instantiating the called method");
        } catch (NoSuchMethodException e) {
            return APIResult.newFailResult(5000,"Method cannot be found");
        } catch (InvocationTargetException e) {
            return APIResult.newFailResult(5000,"There was an error in the method called");
        }
    }
}
