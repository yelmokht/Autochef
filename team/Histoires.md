# Histoires
Informations récapitulatives concernant les différentes histoires.

#### Quelques précisions
Un point correspond à une heure de travail par binôme (approximatif).  Par itération il faut accomplir 138 points.

----------------------


## Pondération

| Priorité/3 | N°                 | Description                        | Difficulté/3 | Risque/3 | Heures/? | Points |
|------------|--------------------|------------------------------------|--------------|----------|----------|--------|
| 1          | [1](#Histoire 1)   | Liste de course                    | 7            | 2        | 50       | /      |
|            | [2](#Histoire 2)   | Recettes                           | 6            | 1        | 40       |        |
|            | [3](#Histoire 3)   | Menu de Semaine                    | 5            | 1        | 40       |        |
| 2          | [4](#Histoire 4)   | Exportation pdf                    | 5            | 2        | 20       |        |
|            | [5](#Histoire 5)   | Gestion Magasins                   | 5            | 3        | 70       | /      |
|            | [6](#Histoire 6)   | Recomendation Magasin              | 7            | 1        | 20       |        |
|            | [10](#Histoire 10) | Ajout des produits dans un Magasin | 7            | 1        | 10       |        |
|            | [12](#Histoire 12) | Section aide                       | 7            | 1        | 20       |        |
| 3          | [7](#Histoire 7)   | Prix minimum pour une liste        | 7            | 1        | 40       |        |
|            | [8](#Histoire 8)   | Calcul du plus court chemin        | 7            | 1        | 20       |        |
|            | [9](#Histoire 9)   | Login User                         | 7            | 1        | 15       |        |
|            | [11](#Histoire 11) | Valeur nutritionnelle              | 7            | 1        | 5       |        |
|            | [13](#Histoire 13) | App android                        | 7            | 1        | 30       |        |
|            | [14](#Histoire 14) | Remember the Milk                  | 7            | 1        | 20       |        |

----------------------


## Description

### Histoire 1

**Instructions originales:**          
L’utilisateur doit pouvoir créer et modifier une liste de courses comportant des produits disponibles dans une base de données générique. Pour chaque produit apparaissant dans la liste, il faut pouvoir
indiquer le nombre ou la quantité désirée de ce produit. La quantité doit pouvoir être encodée manuellement ou se faire à l’aide de
boutons "+" et "-".
L’utilisateur doit pouvoir archiver une liste de courses.

**Tâches en plus:**          
- TODO

:question: **Question:**       
- TODO


### Histoire 2

**Instructions originales:**\
L’utilisateur doit pouvoir gérer (créer/supprimer/modifier) sa liste
de recettes. Les informations minimales d’une recette sont:
- son nom
- les ingrédients
- le nombre de personnes
- le type de plat (mijoté, quiche, entrée, boisson, . . . )
- les instructions de préparation
- si le plat est végétarien, contient de la viande ou du poisson.\
L’utilisateur doit aussi pouvoir importer des recettes au format
JSON depuis un fichier.

**Tâches en plus:**
- ajout option vegan

:question: **Question:**
- TODO


### Histoire 3

**Instructions originales:**\
L’utilisateur doit pouvoir créer une liste de menus sur une durée
déterminée, par exemple une semaine. Pour ce faire, l’utilisateur
doit pouvoir effectuer une recherche dans ses recettes à l’aide de
filtres, et ajouter ou supprimer des recettes pour chaque jour. Il
peut y avoir plusieurs recettes prévues pour un même jour, et un
nombre de personnes différent pour chaque recette.
De plus, l’utilisateur doit pouvoir compléter automatiquement sa
liste de menus hebdomadaire avec d’autres recettes enregistrées.
La génération automatique doit au minimum prendre en compte
le nombre de menus végétariens, de viande et de poisson. Si une
recette ne plaît pas à l’utilisateur, celui-ci pourra la remplacer par
une nouvelle suggestion.
Enfin, l’utilisateur doit pouvoir directement générer une liste de
courses depuis une liste de menus.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 4

**Instructions originales:**\
L’utilisateur doit pouvoir exporter sa liste de courses sous un format
standard comme PDF, docx ou odt. Cette liste doit se présenter de
manière ergonomique, par exemple en séparant les produits par
rayon (boucherie, fruits & légumes, conserves, nettoyage, . . . ).
En plus, l’utilisateur doit pouvoir envoyer cette liste de course par
mail depuis l’application à une adresse préférée enregistrée dans
l’application, ou à une adresse saisie manuellement au moment de
l’envoi.
Si possible, l’application fournira une prévisualisation modifiable
avant impression ou envoi.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 5

**Instructions originales:**\
Un utilisateur doit pouvoir rechercher des magasins sur une carte
afin d’en ajouter à la liste des magasins dans lesquels l’utilsateur
fait ses courses.
Un utilisateur doit pouvoir visualiser ses magasins sur une carte,
en ajouter de nouveau ou en supprimer.
Pour chaque magasin enregistré, l’utilisateur peut y ajouter les produits qui s’y vendent et le prix de ces produits.


**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 6

**Instructions originales:**\
Pour une liste de courses donnée, l’utilisateur doit pouvoir consulter
les magasins qui contiennent tous les produits demandés.
Chacun de ces magasins doit être consultable sur une carte avec le
prix de la liste de courses clairement indiqué. Celui qui a le prix le
plus bas et celui le plus proche doivent être mis en évidence.\
Si le magasin le plus proche est beaucoup plus cher qu’un magasin un peu plus loin, l’application peut proposer à l’utilisateur le
magasin le plus proche ainsi que des suggestions de magasins un
peu plus loin mais plus intéressants financièrement. Inversement,
si le magasin le moins cher est très éloigné et qu’il y a un magasin plus proche à un prix raisonnable, celui-ce doit être suggéré à
l’utilisateur en plus du magasin le moins cher.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 7

**Instructions originales:** \
Si tous les produits d’une même liste ne sont pas disponibles dans un
unique magasin, ou s’il est financièrement plus intéressant d’acheter
les produits dans des magasins différents et relativement proches,
l’application doit pouvoir proposer, s’il existe, un ensemble de magasins à visiter. Si l’application trouve un tel ensemble de magasin,
l’utilisateur doit pouvoir :
1. obtenir la valeur du gain financier obtenu en achetant ses
   produits dans différents magasins plutôt que dans un seul,
2. obtenir une liste indiquant quels produits sont à acheter dans
   quel magasin,
3. pointer sa position de départ et d’arrivée sur une carte (ou
   encoder des coordonnées GPS) et obtenir le tour le plus court
   passant par tous ces magasins, afficher ce tour sur une carte
   et obtenir une description des directions à suivre pour parcourir ce tour.
   L’application veillera à éviter de proposer des tours énormes fournissant un gain financier trop faible.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 8

**Instructions originales:**\
Sur base d’une position de départ de l’utilisateur et d’un magasin
donné, l’utilisateur doit pouvoir obtenir le plus court chemin jusqu’au magasin sur une carte. Les positions de départ et d’arrivée
de l’utilisateur doivent pouvoir être pointées par l’utilisateur sur la
carte ou être récupérées via ses coordonnées GPS. Ce plus court
chemin doit pouvoir être affiché sur la carte et donner sa longueur
exacte et son temps de parcours à pied ou à vélo.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 9

**Instructions originales:**\
En démarrant le programme, un visiteur peut créer un nouveau
compte en indiquant ses données personnelles (par exemple nom
d’utilisateur, mot de passe, domicile). Un utilisateur connecté peut
accéder à son historique de listes de courses, à des recettes favorites.
L’interface graphique du programme devra offrir la possibilité de se
déconnecter du système à tout moment.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO

### Histoire 10

**Instructions originales:**\
Après avoir correctement repéré un magasin sur la carte, l’utilisateur doit pouvoir ajouter au magasin les informations concernant
les produits qui y sont vendus. Pour ce faire, l’utilisateur peut :
1. Encoder à la main les informations pour chaque produit à
   l’aide d’une interface prévue à cet effet.
2. Importer dans le logiciel un ensemble de données structurées
   créé à l’avance.
   Dans tous les cas, le logiciel doit signaler à l’utilisateur la présence
   de données incohérentes ou invalides.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 11

**Instructions originales:**\
Les valeurs nutritionnelles des produits doivent être disponibles.
L’application doit pouvoir fournir les valeurs nutritionelles (par personne) d’une recette donnée et de chaque ingrédient la composant.
Les valeurs nutritionnelles considérées sont le nombre de calories
ainsi que la quantité de protéines, de lipides et de glucides.\
Lorsqu’une liste de menus est planifiée pour plusieurs jours, l’application doit montrer la valeur nutritionelle moyenne et totale par
jour.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 12

**Instructions originales:**\
Les visiteurs/utilisateurs peuvent accéder à une section d’aide dans
le programme. Avec cet outil, les visiteurs/utilisateurs peuvent
obtenir des informations plus détaillées et des explications sur
comment utiliser les différentes fonctionnalités offertes par le programme. Éventuellement, un tutoriel démontrant l’utilisation de
certaines fonctionnalités pourrait être démarré à partir de cette
section.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 13

**Instructions originales:**\
Les utilisateurs peuvent envoyer une liste de courses vers une version simplifiée du programme sur smartphone. Cette version simplifiée reprend les fonctionnalités de gestion des listes de courses
pour permettre leur utilisation en version nomade, et peut se synchroniser avec l’application principale pour récupérer les listes de
courses encodées.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO


### Histoire 14

**Instructions originales:**\
Les utilisateurs peuvent envoyer une liste de course vers leur compte
remember the milk™.

**Tâches en plus:**
- TODO

:question: **Question:**
- TODO

