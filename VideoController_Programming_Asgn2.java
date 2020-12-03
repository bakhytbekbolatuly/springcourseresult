package org.magnum.mobilecloud.video;

import org.magnum.mobilecloud.video.client.SecuredRestBuilder;
import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import retrofit.RestAdapter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Controller
public class VideoController {

    private ArrayList<Video> listVideo = new ArrayList<>();

    private final String TEST_URL = "http://localhost:8080";

    private final String USERNAME1 = "admin";
    private final String USERNAME2 = "user0";
    private final String PASSWORD = "pass";
    private final String CLIENT_ID = "mobile";

    private VideoSvcApi readWriteVideoSvcUser1 = new SecuredRestBuilder()
            .setEndpoint(TEST_URL)
            .setLoginEndpoint(TEST_URL + VideoSvcApi.TOKEN_PATH)
            .setLogLevel(RestAdapter.LogLevel.NONE)
            .setUsername(USERNAME2).setPassword(PASSWORD).setClientId(CLIENT_ID)
            .build().create(VideoSvcApi.class);

    @RequestMapping(value = "/video", method = RequestMethod.POST)
    public @ResponseBody Video getVideo(@RequestBody Video v) {
        Video video = new Video();

        long ID = listVideo.size() + 1;

        video.setId(ID);
        video.setName(v.getName());
        video.setDuration(v.getDuration());
        video.setLikes(0);
        video.setUrl(v.getUrl());

        listVideo.add(video);

        return video;
    }

    @RequestMapping(value = "/video", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Video> getCollectionVideo() {
        return listVideo;
    }

    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
        if (id > 0) {
            for (Video itemVideo : listVideo) {
                if (itemVideo.getId() == id) {
                    return itemVideo;
                }
            }
        } else {
            response.sendError(404);
        }

        return null;
    }

    @RequestMapping(value = "/video/{id}/like", method = RequestMethod.POST)
    public void likeVideo(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
        if (id > 0) {
            for (Video itemVideo : listVideo) {
                if (itemVideo.getId() == id) {
                    if (itemVideo.getLikes() == 0) {
                        itemVideo.setLikes(1);
                    } else {
                        response.sendError(400);
                    }
                }
            }
        } else {
            response.sendError(404);
        }
    }

    @RequestMapping(value = "/video/{id}/unlike", method = RequestMethod.POST)
    public void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
        if (id > 0) {
            for (Video itemVideo : listVideo) {
                if (itemVideo.getId() == id) {
                    itemVideo.setLikes(0);
                }
            }
        } else {
            response.sendError(404);
        }
    }

    @RequestMapping(value = "/video/search/findByName", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Video> searchByTitle (@RequestParam("title") String title) {
        ArrayList<Video> list = new ArrayList<>();

        for (Video itemVideo : listVideo) {
            if (itemVideo.getName().equals(title)) {
                list.add(itemVideo);
            }
        }

        return list;
    }

    @RequestMapping(value = "/video/search/findByDurationLessThan", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Video> getDuration(@RequestParam("duration") long duration) {
        ArrayList<Video> list = new ArrayList<>();

        for (Video itemVideo : listVideo) {
            if (itemVideo.getDuration() < duration) {
                list.add(itemVideo);
            }
        }

        return list;
    }
}
