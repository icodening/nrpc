package cn.icodening.nrpc.example;

import cn.icodening.nrpc.example.api.IEchoService;
import cn.icodening.nrpc.example.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author icodening
 * @date 2021.03.21
 */
public class EchoService implements IEchoService {

    private final List<Student> students = new ArrayList<>();

    public EchoService() {
        students.add(new Student("Abby", 20));
        students.add(new Student("Mary", 21));
        students.add(new Student("Lihua", 22));
        students.add(new Student("HanMeimei", 23));
    }

    @Override
    public String echo(String hello) {
        return "server port: " + System.getProperty("port") + ", nrpc provider echo: " + hello;
    }

    @Override
    public List<Student> findStudent(String name) {
        return students.stream().filter(student -> student.getName().contains(name)).collect(Collectors.toList());
    }

    @Override
    public Student getOne(String name) {
        List<Student> collect = students.stream().filter(student -> student.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        if (collect.isEmpty()) {
            return null;
        }
        return collect.get(0);
    }

}
