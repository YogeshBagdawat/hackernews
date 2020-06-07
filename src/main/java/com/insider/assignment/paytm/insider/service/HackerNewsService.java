package com.insider.assignment.paytm.insider.service;

import com.insider.assignment.paytm.insider.model.ResponseComment;
import com.insider.assignment.paytm.insider.model.ResponseStory;

import java.util.List;

public interface HackerNewsService {
    List<ResponseStory> getTopStories();

    List<ResponseStory> getPastStories(long start);

    List<ResponseComment> getComments(long id);
}
