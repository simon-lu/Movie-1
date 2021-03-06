package com.dream.service.impl;

import com.dream.common.E3Result;
import com.dream.mapper.MovieMapper;
import com.dream.mapper.SimilartabMapper;
import com.dream.po.*;
import com.dream.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServicelmpl implements MovieService {
    @Autowired
    private MovieMapper moivemapper;
    @Autowired
    private SimilartabMapper similartabMapper;

    @Override
    public E3Result GetAllMoive() {
        MovieExample example = new MovieExample();
        MovieExample.Criteria criteria = example.createCriteria();
        // 执行查询
        List<Movie> list = moivemapper.selectByExample(example);
        if (list == null || list.size() == 0) {
            return E3Result.build(400, "获取电影信息错误");
        }
        return E3Result.ok(list);

    }

    @Override
    public E3Result SortMoiveBycategory(Selectquery query) {
        MovieExample example = new MovieExample();
        MovieExample.Criteria criteria = example.createCriteria();
        // 执行查询
        List<Movie> list = moivemapper.selectBycategory(query);
        return E3Result.ok(list);
    }

    @Override
    public E3Result SortMoiveByMovieid(int id) {
        // 执行查询
        Movie movie = moivemapper.selectByPrimaryKey(id);
        return E3Result.ok(movie);
    }

    @Override
    public E3Result Select5SimilarMoviesById(int id) {
        SimilartabExample similartabExample = new SimilartabExample();
        SimilartabExample.Criteria criteria = similartabExample.createCriteria();
        criteria.andItemid1EqualTo(id);
        similartabExample.setOrderByClause("similar");
        List<Similartab> list = similartabMapper.select5ByExample(similartabExample);
        List<Movie> movieList = new ArrayList<Movie>();
        Movie movie = null;
        for (Similartab similartab : list) {
            movie = moivemapper.selectByPrimaryKey(similartab.getItemid2());
            if (movie != null) {
                movieList.add(movie);
            }
        }
        return E3Result.ok(movieList);
    }

    @Override
    public Movie getMovieByMovieid(Integer id) {
        Movie movie = moivemapper.selectByPrimaryKey(id);
        return movie;
    }


}
