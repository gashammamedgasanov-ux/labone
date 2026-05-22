package storage;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.entity.Solution;
import domain.entity.User;

import java.util.Map;


 // Класс-обертка для сохранения всех данных в один JSON файл.
 // Содержит все коллекции из менеджеров.

public class LabData {

    private Map<Long, Solution> solutions;
    private Map<Long, Preparation> preparations;
    private Map<Long, PreparationComponent> components;
    private Map<String, User> users;

    public LabData() {
    }


    public LabData(Map<Long, Solution> solutions,
                   Map<Long, Preparation> preparations,
                   Map<Long, PreparationComponent> components) {
        this.solutions = solutions;
        this.preparations = preparations;
        this.components = components;
    }


    public Map<Long, Solution> getSolutions() {
        return solutions;
    }

    public Map<Long, Preparation> getPreparations() {
        return preparations;
    }

    public Map<Long, PreparationComponent> getComponents() {
        return components;
    }

    public Map<String, User> getUsers() { return users; }

    public void setSolutions(Map<Long, Solution> solutions) {
        this.solutions = solutions;
    }

    public void setPreparations(Map<Long, Preparation> preparations) {
        this.preparations = preparations;
    }

    public void setComponents(Map<Long, PreparationComponent> components) {
        this.components = components;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}

