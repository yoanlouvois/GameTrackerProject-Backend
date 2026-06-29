# GameTracker Backend

## Présentation

GameTracker est une plateforme web full-stack dédiée aux jeux HTML5 en ligne. Ce dépôt contient le backend de l'application, développé avec **Spring Boot** et **MySQL**.

Le backend expose les différentes API nécessaires au fonctionnement de la plateforme : authentification des utilisateurs, gestion des profils joueurs, système d'amis, gestion des jeux, recommandations et trophées.

L'application s'appuie sur une base de données **MySQL** exécutée dans un conteneur **Docker**, facilitant le déploiement et la reproductibilité de l'environnement de développement.

<p align="center">
  <img width="75%" height="410" alt="liste" src="https://github.com/user-attachments/assets/b8f26954-b787-4278-b6be-98117872763e" />
</p>


**Dépôt Frontend**

Lien vers le dépôt frontend : https://github.com/yoanlouvois/Front-End-GameTracker

**Infrastructure**

Lien vers le dépôt de l'infrastructure : https://github.com/yoanlouvois/GameTrackerProject-Infra

---

## Fonctionnalités

Le backend fournit les services nécessaires au fonctionnement de l'application :

### Authentification

* Création de compte
* Connexion utilisateur
* Gestion des sessions et des comptes

### Gestion des Joueurs

* Gestion des profils utilisateurs
* Consultation des statistiques
* Gestion des trophées

### Gestion des Amis

* Ajout et suppression d'amis
* Consultation des relations entre utilisateurs

### Gestion des Jeux

* Consultation du catalogue de jeux
* Gestion des jeux récents
* Suivi de l'activité des joueurs

### Recommandations

* Envoi de recommandations de jeux
* Consultation des recommandations reçues

---

## Documentation API

Une documentation Swagger est disponible afin de faciliter l'exploration et le test des différents endpoints REST exposés par l'application.

<p align="center">
  <img width="60%" alt="image" src="https://github.com/user-attachments/assets/986b7514-9740-460b-ace2-57bdbc5c7ead" />
</p>

---

## Technologies utilisées

### Backend

* Java
* Spring Boot
* Swagger / OpenAPI

### Base de données

* MySQL
* Docker

### Outils

* Maven
* Docker Compose

---


# Configuration de l'environnement GameTracker

## Étape 1 : Lancer la base de données avec Docker

Avant de démarrer l'application GameTracker, vous devez d'abord initialiser la base de données en utilisant Docker Compose.

Naviguez vers le dossier racine du projet où se trouve le fichier compose.yaml
```bash
cd /chemin/vers/le/projet
```
Lancez le conteneur Docker pour la base de données (ou simplement run le compose.yaml)
docker-compose up -d

Cette commande va créer et démarrer un conteneur Docker avec la base de données configurée selon les paramètres définis dans le fichier compose.yaml.

Note : Assurez-vous que Docker Desktop est installé et en cours d'exécution sur votre machine avant de lancer cette commande.

## Étape 2 : Démarrer l'application Spring Boot

Une fois la base de données lancée et disponible, vous pouvez démarrer le backend de l'application GameTracker.

Naviguez vers le dossier du projet Spring Boot
```bash
cd /chemin/vers/le/backend
```
Démarrez l'application Spring Boot
```bash
./mvnw spring-boot:run
```
Ou si vous utilisez un IDE comme IntelliJ IDEA ou Eclipse, vous pouvez ouvrir le projet et exécuter la classe principale GameTrackerProjectApplication.
