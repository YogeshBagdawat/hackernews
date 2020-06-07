package com.insider.assignment.paytm.insider.service;

import com.insider.assignment.paytm.insider.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
public class HackerNewsServiceImpl implements HackerNewsService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${resource.url.top}")
    private String topStoriesUrl;

    @Value("${resource.url.item}")
    private String itemUrl;

    @Value("${resource.url.maxId}")
    private String maxIdUrl;

    @Value("${resource.url.user}")
    private String userUrl;

    private Map<Long, Story> map = new HashMap<>();

    List<ResponseStory> stories = new ArrayList<>();

    private long lastApiCalltime = 0;

    @Override
    public List<ResponseStory> getTopStories() {
        long[] topStoryIds = new long[500];
        if (lastApiCalltime == 0 || lastApiCalltime <= System.currentTimeMillis()) {
            lastApiCalltime = System.currentTimeMillis() + (10 * 60 * 1000);
            topStoryIds = restTemplate.getForObject(topStoriesUrl, long[].class);
            stories.clear();
            List<CompletableFuture<ResponseStory>> storyFurures = new ArrayList<>();
            for (int i =0;i<10;i++){
                storyFurures.add(getStoryAsync(topStoryIds[i], stories));
            }
            for (CompletableFuture complet:storyFurures
                 ) {
                try {
                    stories.add((ResponseStory) complet.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(stories, Comparator.comparing(ResponseStory::getScore, Comparator.reverseOrder()));
        }
        return stories;
    }

    @Async
    public CompletableFuture<ResponseStory> getStoryAsync(long topStoryId, List<ResponseStory> stories) {
        ResponseStory story;
        story = restTemplate.getForObject(itemUrl + topStoryId + ".json", ResponseStory.class);
        return CompletableFuture.completedFuture(story);
    }

    @Override
    public List<ResponseStory> getPastStories(long start) {
        List<ResponseStory> stories = new ArrayList<>();
        List<CompletableFuture<ResponseStory>> storyFurures = new ArrayList<>();
        for (int i =0;i<10;i++){
            start = start-i;
            storyFurures.add(getStoryAsync(start, stories));
        }
        for (CompletableFuture complet:storyFurures
        ) {
            try {
                stories.add((ResponseStory) complet.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return stories;
    }



    @Override
    public List<ResponseComment> getComments(long id) {
        List<ResponseComment> responseComments = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        Story story = restTemplate.getForObject(itemUrl + id + ".json", Story.class);
        long[] ids = story.getKids();
        if (ids==null||ids.length==0){
            return responseComments;
        }
        List<CompletableFuture<Comment>> commentFutures = new ArrayList<>();
        for (int i=0;i<ids.length&&i<10;i++) {
            commentFutures.add(getComment(ids[i]));
        }
        for (int i = 0; i < 10; i++) {
            try {
                comments.add(commentFutures.get(i).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(comments,Comparator.comparing(Comment::getNumberOfKids,Comparator.reverseOrder()));
        List<CompletableFuture<ResponseComment>> responeFuture = new ArrayList<>();
        for (Comment comment: comments
        ) {
            responeFuture.add(getUser(comment));
        }
        //CompletableFuture.allOf(responeFuture.toArray(new CompletableFuture[0])).join();
        for (int i = 0; i < 10; i++) {
            try {
                responseComments.add(responeFuture.get(i).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return responseComments;
    }

    @Async
    public CompletableFuture<Comment> getComment(long id1) {
        Comment comment = restTemplate.getForObject(itemUrl + id1 + ".json", Comment.class);
        if (comment.getKids()==null){
            comment.setNumberOfKids(0);
        }else {
            comment.setNumberOfKids(comment.getKids().length);
        }
        return CompletableFuture.completedFuture(comment);
    }

    @Async
    public CompletableFuture<ResponseComment> getUser(Comment comment) {
        ResponseUser user =  restTemplate.getForObject(userUrl+comment.getBy()+".json", ResponseUser.class);
        ResponseComment responseCommnet = new ResponseComment();
        responseCommnet.setText(comment.getText());
        responseCommnet.setUserHNHandle(comment.getBy());
        responseCommnet.setAge(getAge(user.getAge()));
        return CompletableFuture.completedFuture(responseCommnet);
    }

    private long getAge(long create) {
        long age = 0;
        long created = create*1000L;
        long current = System.currentTimeMillis();
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int d1 = Integer.parseInt(formatter.format(created));
        int d2 = Integer.parseInt(formatter.format(current));
        age = (d2 - d1) / 10000;;
        return age;
    }
}
