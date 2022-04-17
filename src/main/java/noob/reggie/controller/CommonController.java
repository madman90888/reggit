package noob.reggie.controller;

import noob.reggie.domain.pojo.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("upload")
    public R<String> upload(MultipartFile file) throws IOException {
        final String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;
        file.transferTo(new File(basePath + fileName));
        return R.success(fileName);
    }

    @GetMapping("download")
    public void download(String name, HttpServletResponse response) throws IOException {
        final File file = new File(basePath + name);
        if (file.exists()) {
            response.setContentType("image/jpeg");
            try(final FileInputStream fis = new FileInputStream(file)) {
                int len = 0;
                byte[] bytes = new byte[1024];
                final ServletOutputStream os = response.getOutputStream();
                while ((len = fis.read(bytes)) > 0) {
                    os.write(bytes, 0, len);
                    os.flush();
                }
                os.close();
            }
        }
    }
}
