package org.magnum.dataup;

import org.apache.commons.io.IOUtils;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Controller
public class VideoController {
    private ArrayList<Video> listVideo = new ArrayList<>();

    private String getDataUrl(long videoId){
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
                "http://"+request.getServerName()
                        + ((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
        return base;
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Video> getVideo (Video v) {
        Video video = v.create().withContentType("video/mp4")
                .withDuration(123).withSubject(UUID.randomUUID().toString())
                .withTitle(UUID.randomUUID().toString())
                .build();

        long ID = listVideo.size() + 1;

        video.setId(ID);
        video.setDataUrl(getDataUrl(ID));
        video.setLocation("/video/" + ID + ".mp4");

        return listVideo;
    }

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody Video addVideo (@RequestBody Video v) {
        Video video = new Video();

        int ID = listVideo.size() + 1;

        video.setId(ID);
        video.setDataUrl(getDataUrl(ID));

        video.setLocation(v.getLocation());
        video.setContentType(v.getContentType());
        video.setDuration(v.getDuration());
        video.setTitle(v.getTitle());
        video.setSubject(v.getSubject());

        listVideo.add(video);

        return video;
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.GET)
    public @ResponseBody
    void getVideoData(@PathVariable("id") long id, HttpServletResponse response) throws IOException {

        if (id > 0) {
            response.getOutputStream().write(IOUtils.toByteArray(new FileInputStream("src/test/resources/test.mp4")));
            response.setContentType("video/mp4");
        } else {
            response.sendError(404);
        }
    }

    @RequestMapping(value = "/video/{id}/data", method = RequestMethod.POST)
    public @ResponseBody
    VideoStatus getVideoData(@PathVariable("id") long id, @PathVariable("data") MultipartFile VideoData, HttpServletResponse response) throws IOException {

        if (id > 0) {
            response.sendError(200);
        } else {
            response.sendError(404);
        }

        return new VideoStatus(VideoStatus.VideoState.READY);
    }
}
