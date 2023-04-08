package UserInterface;

import Application.IncorrectPermissionException;
import Application.UpdateDependenciesController;
import Domain.ProjectNotFoundException;
import Domain.ProjectProxy;
import Domain.ProjectStatus;

import java.util.Scanner;

public class UpdateDependenciesUI {
    private final UpdateDependenciesController controller;

    public UpdateDependenciesUI(UpdateDependenciesController controller){
        this.controller = controller;
    }

    private UpdateDependenciesController getController(){
        return controller;
    }

    public void updateDependencies(){
        if (!getController().updateDependenciesPreconditions()){
            System.out.println("ERROR: You must be a project manager to call this function");
            return;
        }

        try {
            Scanner scanner = new Scanner(System.in);

            while(true){
                showOngoingProjects();

                System.out.println("Give the name of the project you want to edit:");
                String projectName = scanner.nextLine();
                if (projectName.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }

                try{
                    ProjectProxy projectData = getController().getProjectData(projectName);

                    while(true){
                        showRelevantTasks(projectData);
                    }
                } catch (ProjectNotFoundException e) {
                    System.out.println("ERROR: The given project name could not be found.");
                }


            }
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void showOngoingProjects() throws IncorrectPermissionException {
        System.out.println("***** UNFINISHED PROJECTS *****");
        if (getController().getTaskManSystemData().getProjectNames().size() == 0){
            System.out.println(" --- There are no unfinished projects in the system");
        }
        for (String projectName : getController().getTaskManSystemData().getProjectNames()){
            try{
                if (getController().getProjectData(projectName).getStatus() == ProjectStatus.ONGOING){
                    System.out.println(" - " + projectName);
                }
            } catch (ProjectNotFoundException e) {
                throw new RuntimeException(); // TODO mag echt niet gebeuren!
            }
        }
        System.out.println();
    }

    private void showRelevantTasks(ProjectProxy projectData){
        System.out.println("***** (UN)AVAILABLE TASKS *****");
        if (projectData.getActiveTasksNames().size() == 0){
            System.out.println("There are no ");
            return;
        }

        for (String taskName : projectData.getActiveTasksNames()){

        }
    }
}
