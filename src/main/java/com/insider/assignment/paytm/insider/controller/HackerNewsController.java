package com.insider.assignment.paytm.insider.controller;

import com.insider.assignment.paytm.insider.model.ResponseComment;
import com.insider.assignment.paytm.insider.model.ResponseStory;
import com.insider.assignment.paytm.insider.service.HackerNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v0")
@Api(value = "HackerNewsResource")
public class HackerNewsController {

    @Autowired
    HackerNewsService hackerNewsService;

    @ApiOperation(httpMethod = "GET", value = "Get top ten stories", response = ResponseStory.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Top ten stories not found"),
            @ApiResponse(code = 500, message = "The stories could not be fetched")
    })
    @GetMapping("/topstories")
    public ResponseEntity<List<ResponseStory>> topStories() {
        try {
            return new ResponseEntity<>(hackerNewsService.getTopStories(), HttpStatus.OK);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No content found", exception);
        }
        //return "this is top stories";
    }

    @ApiOperation(httpMethod = "GET", value = "Get top ten comments", response = ResponseComment.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Top ten comments not found"),
            @ApiResponse(code = 500, message = "The comments could not be fetched")
    })
    @GetMapping("/comments")
    public ResponseEntity<List<ResponseComment>> comment(@RequestParam("storyId") long stroyId) {
        try {
            return new ResponseEntity<>(hackerNewsService.getComments(stroyId), HttpStatus.OK);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No content found", exception);
        }
    }

    @ApiOperation(httpMethod = "GET", value = "Get past ten stories", response = ResponseStory.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Top past ten stories not found"),
            @ApiResponse(code = 500, message = "The stories could not be fetched")
    })
    @GetMapping("/paststories")
    public ResponseEntity<List<ResponseStory>> pastStories(@RequestParam(required = false, defaultValue = "0", name = "start") long start) {
        try {
            return new ResponseEntity<>(hackerNewsService.getPastStories(12301480), HttpStatus.OK);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No content found", exception);
        }
    }

}
