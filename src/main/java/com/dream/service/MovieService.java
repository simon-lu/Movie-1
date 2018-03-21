package com.dream.service;
import com.dream.common.E3Result;
import com.dream.po.Movie;
import com.dream.po.Selectquery;
public interface MovieService {
    E3Result GetAllMoive();
    E3Result SortMoiveBycategory(Selectquery query);
    E3Result SortMoiveByMovieid(int id);
    E3Result Select5SimilarMoviesById(int id);
    Movie getMovieByMovieid(Integer id);
}
