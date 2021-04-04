package cn.icodening.nrpc.example.api;

import cn.icodening.nrpc.example.model.Student;

import java.util.List;

/**
 * @author icodening
 * @date 2021.03.21
 */
public interface IEchoService {

    String echo(String hello);

    List<Student> findStudent(String name);

    Student getOne(String name);

}
