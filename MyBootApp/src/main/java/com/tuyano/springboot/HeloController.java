package com.tuyano.springboot;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.tuyano.springboot.repositories.MyDataRepository;

@Controller
public class HeloController {

	@Autowired
	MyDataRepository repository;

	@PersistenceContext
	EntityManager entityManager;

	MyDataDaoImpl dao;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index(ModelAndView mav) {
		mav.setViewName("index");
		mav.addObject("msg", "MyDataのサンプルです.");
		Iterable<MyData> list = dao.findByAge(10, 40);
		mav.addObject("datalist", list);
		return mav;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@Transactional(readOnly = false)
	public ModelAndView form(@ModelAttribute("formModel") @Validated MyData mydata, BindingResult result,
			ModelAndView mov) {
		ModelAndView res = null;
		if (!result.hasErrors()) {
			repository.saveAndFlush(mydata);
			res = new ModelAndView("redirect:/");
		} else {
			mov.setViewName("index");
			mov.addObject("msg", "sorry, error is occured");
			Iterable<MyData> list = repository.findAll();
			mov.addObject("detalist", list);
			res = mov;
		}
		return res;
	}

	/*
	 * @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET) public
	 * ModelAndView edit(@ModelAttribute MyData mydata,
	 *
	 * @PathVariable int id, ModelAndView mav) { mav.setViewName("edit");
	 * mav.addObject("title", "edit mydata."); MyData data =
	 * repository.findById((long) id); mav.addObject("formModel", data); return
	 * mav; }
	 *
	 * @RequestMapping(value = "/edit", method = RequestMethod.POST)
	 *
	 * @Transactional(readOnly = false) public ModelAndView
	 * update(@ModelAttribute MyData mydata, ModelAndView mav) {
	 * repository.saveAndFlush(mydata); return new ModelAndView("redirect:/"); }
	 *
	 * @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	 * public ModelAndView delete(@PathVariable int id, ModelAndView mav) {
	 * mav.setViewName("delete"); mav.addObject("title", "delete mydata.");
	 * MyData data = repository.findById((long) id); mav.addObject("formModel",
	 * data); return mav; }
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)

	@Transactional(readOnly = false)
	public ModelAndView remove(@RequestParam long id, ModelAndView mav) {
		repository.delete(id);
		return new ModelAndView("redirect:/");
	}

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public ModelAndView find(ModelAndView mav) {
		mav.setViewName("find");
		mav.addObject("title", "find page");
		mav.addObject("msg", "MyDataのサンプルです.");
		mav.addObject("value", "");
		Iterable<MyData> list = dao.getAll();
		mav.addObject("datalist", list);
		return mav;
	}

	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView search(HttpServletRequest request, ModelAndView mav) {
		mav.setViewName("find");
		String param = request.getParameter("fstr");
		if (param == "") {
			mav = new ModelAndView("redirect:/find");
		} else {
			mav.addObject("title", "find result");
			mav.addObject("msg", "「" + param + "」の結果");
			mav.addObject("value", param);
			List<MyData> list = dao.find(param);
			mav.addObject("datalist", list);
		}
		return mav;
	}

	@PostConstruct
	public void init() {
		dao = new MyDataDaoImpl(entityManager);
		MyData d1 = new MyData();
		d1.setName("tuyano");
		d1.setAge(123);
		d1.setMail("syoda@tuyano.com");
		d1.setMemo("this is my data");
		repository.save(d1);
		MyData d2 = new MyData();
		d2.setName("hanako");
		d2.setAge(15);
		d2.setMail("hanako@flower");
		d2.setMemo("my girl friend");
		repository.save(d2);
		MyData d3 = new MyData();
		d3.setName("sachiko");
		d3.setAge(37);
		d3.setMail("sachiko@happy");
		d3.setMemo("my work friend");
		repository.save(d3);
	}
}

class DataObject {

	private int id;
	private String name;
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public DataObject(int id, String name, String value) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
	}
}
