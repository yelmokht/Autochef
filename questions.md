## Question 1 - Relation client

Lors des différentes itérations, vous avez parfois pris du temps pour faire du refactoring de code d'itérations précédentes. 
Comment justifier ce temps auprès de client, qui vous paye pour celui-ci et attends de son produit qu'il se développe en terme de fonctionalités ?

## Question 2 - Base de données

Quelle stratégie avez-vous utilisée pour la gestion de votre base de données ? Comment cette stratégie a-t-elle évolué au cours du projet? 

## Question 3 - Architecture

Comme discuté au cours des dernières réunions, vote architecture initiale basée sur des accès directs à la base de données a pu s'avérer
sous-optimal. Comment cela s'est-il manifesté au cours de l'évolution du programme ? Comment y avez-vous remédié (ou essayé d'y remédier),
et quelles difficultés avez-vous rencontré lors de ces modification ?

## Question 4 - Tests

Dans votre projet, toutes les classes/méthodes/conditions ne sont pas testées. Pouvez-vous faire la différence entre celles dont l'absence de tests se justifie
et celle pour lesquelles cette absence est problématique ? Justifiez.

## Question 5 - Mails

Quel stratégie avez-vous mise en place pour la génération et l'envoi des mails ? Quelles sont ses limitations ? Cette stratégie pourrait-elle être utilisée
dans une application en phase de production ? Pourquoi, et le cas échéant, comment pourriez-vous la rendre utilisable ?

## Question 6 - Design pattern

Quel design pattern avez-vous utilisés pour la gestion de la base de données ? Quels sont les cas d'usage ainsi que les limitations/désavantages de
celui-ci? Vous avez recodé un ORM (SQLModel, SQLBuilder, ...). Pourquoi n'avoir pas pris une librairie pour cela ?

## Question 7 - MVC

Lors des dernières réunions, il vous a été conseillé d'implémenter des ViewControllers, et d'implémenter les méthodes liées aux évènements via des interfaces,
conseil que vous avez suivi comme le montre la déclaration de classe suivante:

```java
	public class CalendarController implements CalendarViewerController.Listener
```
	
Quel est l'avantage de l'implémentation suivante ?

Quelles sont les classes responsables de charger les FXML ? Est-ce toujours uniformément appliqué ?

## Question 8 - OO

Votre classe ```FieldUtils``` ne définit que des méthodes statiques. Comment situez-vous cette pratique par rapport aux règles de l'OOP ? Pourquoi?

## Question 9 - Gestion des FXML

Comment avez-vous géré le chargement des FXML, entre vues/controlleurs?

## Question 10 - Gestion des exceptions

Dans le morceau de code suivant, comment justifiez-vous la gestion de l'exception ?

```java
	    public Department getType() {
        try {
            var ingredientModel = Ingredient.newBuilder()
                                            .setName(name)
                                            .build()
                                            .getModel();
            if(ingredientModel.isPresent()){
                return ingredientModel.get().getType();
            }
        } catch (SQLException e) {
            //
        }
        return Department.UNDEFINED;
    }
```
