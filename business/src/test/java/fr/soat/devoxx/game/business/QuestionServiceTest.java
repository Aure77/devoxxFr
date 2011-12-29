/*
 * Copyright (c) 2011 Khanh Tuong Maudoux <kmx.petals@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fr.soat.devoxx.game.business;

import fr.soat.devoxx.game.business.question.QuestionManager;
import fr.soat.devoxx.game.pojo.QuestionResponseDto;
import fr.soat.devoxx.game.pojo.ResponseRequestDto;
import fr.soat.devoxx.game.pojo.ResponseResponseDto;
import fr.soat.devoxx.game.pojo.question.QuestionType;
import fr.soat.devoxx.game.pojo.question.ResponseType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * User: khanh
 * Date: 21/12/11
 * Time: 19:34
 */
public class QuestionServiceTest {
    private QuestionService questionService;

    @Before
    public void setUp() {
        QuestionManager questionManager = QuestionManager.INSTANCE;
        questionManager.setConfiguration("question-test.properties");
        questionService = new QuestionService();
        questionService.questionManager = questionManager;
    }

    @Test
    public void getQuestionShouldReturnAValidResult() {
        QuestionResponseDto questionDto = questionService.getQuestion();
        assertNotNull(questionDto);
        assertNotNull(questionDto.getLabel());
        assertFalse(questionDto.getLabel().isEmpty());
        
        assertNotNull(questionDto.getId());
        assertNotNull(questionDto.getQuestionType());
        assertNotNull(questionDto.getQuestions());

        if (questionDto.getQuestionType() != QuestionType.FREE) {
            assertFalse(questionDto.getQuestions().isEmpty());
        } else {
            assertTrue(questionDto.getQuestions().isEmpty());
        }
        
    }
    
    @Test
    public void giveResponseForQuestion1ShouldReturnSuccessWithValidResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(1);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.SUCCESS, responseResponseDto.getResponseType());
    }

    @Test
    public void giveResponseForQuestion1ShouldReturnFailWithFalseResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(1);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto1");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responses.add("toto");
        responses.add("tata");
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());
    }

    @Test
    public void giveResponseForQuestion1ShouldReturnFailWithInvalidResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(1);
        ArrayList<String> responses = null;
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.INVALID, responseResponseDto.getResponseType());
    }


    @Test
    public void giveResponseForQuestion2ShouldReturnSuccessWithValidResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(2);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.SUCCESS, responseResponseDto.getResponseType());
    }

    @Test
    public void giveResponseForQuestion2ShouldReturnFailWithFalseResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(2);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto1");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responses.add("toto");
        responses.add("tata");
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());
    }


    @Test
    public void giveResponseForQuestion3ShouldReturnSuccessWithValidResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(3);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto");
        responses.add("titi");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.SUCCESS, responseResponseDto.getResponseType());
    }

    @Test
    public void giveResponseForQuestion3ShouldReturnFailWithFalseResponse() {
        ResponseRequestDto responseDto = new ResponseRequestDto();
        responseDto.setId(3);
        ArrayList<String> responses = new ArrayList<String>();
        responses.add("toto");
        responseDto.setResponses(responses);
        ResponseResponseDto responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responses.add("toto");
        responses.add("tata");
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());


        responses = new ArrayList<String>();
        responses.add("toto");
        responses.add("titi");
        responses.add("tutu");
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());

        responses = new ArrayList<String>();
        responseDto.setResponses(responses);
        responseResponseDto = questionService.giveResponse(responseDto);
        assertNotNull(responseResponseDto);
        assertEquals(ResponseType.FAIL, responseResponseDto.getResponseType());
    }


}
