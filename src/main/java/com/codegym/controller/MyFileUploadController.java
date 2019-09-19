package com.codegym.controller;

import com.codegym.form.MyUpLoadForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MyFileUploadController {
    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        Object target = dataBinder.getTarget();
        if (target == null){
            return;
        }
        System.out.println("Target"+target);
        if (target.getClass() == MyUpLoadForm.class){
            dataBinder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        }
    }
    //Get hien thi trang form upload
    @RequestMapping(value = "/upLoadOneFile", method = RequestMethod.GET)
        public String upLoadOneFileHandle(Model model){
        MyUpLoadForm myUpLoadForm = new MyUpLoadForm();
        model.addAttribute("myUpLoadForm",myUpLoadForm);
        return "upLoadOneFile";
    }

    @RequestMapping(value = "/upLoadOneFile" ,method = RequestMethod.POST)
    public String upLoadOneFileHandlePOST (HttpServletRequest request, Model model, @ModelAttribute("myUploadForm") MyUpLoadForm myUpLoadForm){
        return this.doUpLoad(request,model,myUpLoadForm);
    }
    private String doUpLoad (HttpServletRequest request, Model model, MyUpLoadForm myUpLoadForm){
        String description = myUpLoadForm.getDescription();
        System.out.println("Description" + description);

        //thu muc goc upload file
        String uploadRootPath =request.getRealPath("upload");
        System.out.println("uploadRootPath="+uploadRootPath);
        File uploadRootDir = new File(uploadRootPath);
        //tao thu muc goc upload neu no ko ton tai
        if (!uploadRootDir.exists()){
            uploadRootDir.mkdirs();
        }
        CommonsMultipartFile[] fileDatas = myUpLoadForm.getFileDatas();
        Map<File, String> uploadedFiles = new HashMap();
        for (CommonsMultipartFile fileData : fileDatas){
            //ten file goc tai client
            String name = fileData.getOriginalFilename();
            System.out.println("Client File Name = "+name);
            if (name != null && name.length()>0){
                try {
                    // tao file tai server
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator+name);
                    //luong ghi du lieu vao file tren server
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(fileData.getBytes());
                    stream.close();
                    uploadedFiles.put(serverFile, name);
                    System.out.println("Write file:"+serverFile);
                } catch (Exception e){
                    System.out.println("Error write file:"+name);
                }
            }
        }
        model.addAttribute("description",description);
        model.addAttribute("uploadFiles",uploadedFiles);
        return "uploadResult";
    }
}
