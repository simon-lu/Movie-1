package com.dream.controller;
import com.dream.po.*;
import com.dream.common.E3Result;
import com.dream.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by ZXL on 2018/3/1.
 */

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private StarService starService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private BrowseService browseService;

    @RequestMapping("/")
    public String showHomepage( HttpServletRequest request){

        return "Home";
    }


    //主页
    @RequestMapping("/index")
    public String showIndex( HttpServletRequest request){
        //获取所有分类标签
        E3Result e3ResultAllCategory = categoryService.GetAllCategory();
        List<Category> list1 = (List<Category>)e3ResultAllCategory.getData();
        //获取所有电影数据(缺少筛选，默认一次加载20个)
        Integer Categoryid=0;
        Selectquery query=new Selectquery();
        query.setCategoryid(Categoryid);
        query.setmolimit(0);
        query.setSort("numrating");
        E3Result e3ResultAllMoive = movieService.SortMoiveBycategory(query);
        List<Movie> list2 = (List<Movie>)e3ResultAllMoive.getData();
        //设置SEESION
        request.getSession().setAttribute("category",list1);
        request.getSession().setAttribute("movie",list2);
        return "index";
    }

    //电影详情传值
    @RequestMapping("/Customer/Description")
    @ResponseBody
    public String GoMoiveDescription(HttpServletRequest request) {
        //获取用户点击的movieid
        int  movieid=Integer.parseInt(request.getParameter("id"));
        E3Result e3Result1 = movieService.SortMoiveByMovieid(movieid);
        Movie movie = (Movie) e3Result1.getData();
        User user=(User) request.getSession().getAttribute("user");
        if(user!=null)
        {
            E3Result e3Result2 = starService.SortReviewByUseridandMovieid(user.getUserid(), movieid);
            Review review = (Review) e3Result2.getData();
            request.getSession().setAttribute("userstar", review);
        }
        else
        {
            Review review = null;
            request.getSession().setAttribute("userstar", review);
        }
        //设置session
        request.getSession().setAttribute("moviedescription",movie);
        return "success";
    }

    //电影详情
    @RequestMapping("/MovieDescription")
    public String showMoiveDescription(HttpServletRequest request){
        return "MovieDescription";
    }

    //加载更多
    @RequestMapping(value = "/loadingmore", method = RequestMethod.POST)
    @ResponseBody
    public E3Result showloadmore(HttpServletRequest request){
        Integer categoryid= Integer.parseInt(request.getParameter("type"));
        int molimit=Integer.parseInt(request.getParameter("molimit"));
        System.out.print("测试"+categoryid);
        Selectquery query=new Selectquery();
        query.setCategoryid(categoryid);
        query.setmolimit(molimit);
        query.setSort(request.getParameter("sort"));
        E3Result e3ResultAllMoive = movieService.SortMoiveBycategory(query);
        List<Movie> list = (List<Movie>)e3ResultAllMoive.getData();
        E3Result e3Result=E3Result.ok(list);
        return e3Result;
    }

    //选择排序电影
    @RequestMapping(value = "/typesortmovie", method = RequestMethod.POST)
    @ResponseBody
    public E3Result showtypesortmovie(HttpServletRequest request){
        int type= Integer.parseInt(request.getParameter("type"));
        int molimit=Integer.parseInt(request.getParameter("molimit"));
        String sort=request.getParameter("sort");
        Selectquery query=new Selectquery();
        query.setCategoryid(type);
        query.setmolimit(molimit);
        query.setSort(sort);
        E3Result e3ResultAllMoive = movieService.SortMoiveBycategory(query) ;
        List<Movie> list = (List<Movie>)e3ResultAllMoive.getData();
        E3Result e3Result=E3Result.ok(list);
        return e3Result;
    }

    //电影评星
    @RequestMapping(value = "/getstar", method = RequestMethod.POST)
    @ResponseBody
    public String getstar(HttpServletRequest request) throws ParseException {
        int userid = Integer.parseInt(request.getParameter("userid"));
        int movieid = Integer.parseInt(request.getParameter("movieid"));
        Double star = Double.parseDouble(request.getParameter("star"));
        String str = request.getParameter("time");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = format.parse(str);
        System.out.print("测试"+"userid:"+userid+"movieid:"+movieid+"star"+star+"time:"+time);
        Review review = new Review();
        review.setUserid(userid);
        review.setMovieid(movieid);
        review.setStar(star);
        review.setReviewtime(time);
        starService.setStar(review);
        review=null;
        E3Result e3Result = starService.SortReviewByUseridandMovieid(userid, movieid);
         review = (Review) e3Result.getData();
        request.getSession().setAttribute("userstar", review);
        return "success";
    }

    //相似电影
    @RequestMapping(value = "/getSimiMovies", method = RequestMethod.POST)
    @ResponseBody
    public E3Result getSimiMovies(HttpServletRequest request) throws ParseException {
        int id = Integer.parseInt(request.getParameter("id"));
        System.out.println("####################" + id);
        E3Result e3Result = movieService.Select5SimilarMoviesById(id);
        List<Movie> simiMovies = (List<Movie>)e3Result.getData();
        e3Result=E3Result.ok(simiMovies);
        return e3Result;
    }

    // 个人中心
    @RequestMapping(value = "/page/profile")
    @ResponseBody
    public String goProfile(HttpServletRequest request) {
        // 拿到userid
        User user=(User) request.getSession().getAttribute("user");
        Integer userid = user.getUserid();

        List<Review> reviews = reviewService.getReviewListByUserId(userid);
        // 创建喜欢电影list
        List<Movie> movies = new ArrayList<Movie>();
        Browse browse = browseService.getBrowseByUserId(userid);
        if (null != browse.getmovieids()) {
            String movieids = browse.getmovieids();
            String[] strmovieids = movieids.split(",");
            for (String strmovieid: strmovieids) {
                Integer movieid = Integer.parseInt(strmovieid);
                Movie movie = movieService.getMovieByMovieid(movieid);
                movies.add(movie);
            }
        }

        // 为review list中添加电影url
        for (Review review: reviews) {
            Integer movieid = review.getMovieid();
            Movie movie = movieService.getMovieByMovieid(movieid);
            review.setPicture(movie.getPicture());
        }
        request.getSession().setAttribute("movies", movies);
        request.getSession().setAttribute("reviews", reviews);

        // TODO：根据推荐表取值
        request.getSession().setAttribute("rectabs", movies);
        return "success";
    }

    @RequestMapping("/profile")
    public String showProfie() {
        return "profile";
    }
}

