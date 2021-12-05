# ia_monitoring_temperature_humidity
**Application de suivi de température et humidité** :

Cette application a pour but de suivre en temps réel la température et l'humidité ambiante et de les ajuster en fonction des besoins de l'utilisateur.

Permet :
-  d’assurer une meilleure traçabilité de la température
-  de prévenir les risques de surchauffe
-  d’améliorer le confort de l’espaces de travail
-  de réduire la consommation d’énergie

On historise les informations de température/humidité collectées ainsi que les températures/humidité idéales

   
**Scénario** :

Le système essaye de s'assurer que la température ambiante correspond à la température idéale  programmée à chaque plage de la journée.

L'utilisateur a la possibilité de modifier la température idéale en utilisant soit l'interface web, soit le module de reconnaissance vocale.

Le ventilation est actionné si la température est supérieure à la température idéale.
(l'intensité du moteur dépend de la différence constatée entre ces deux température).



**Description de la config matérielle**

- Carte Arduino Uno
- Capteur de température
- Capteur d'humidité
- Ventilateur (actionneur)

 - Machine (PC).

**Description des technos logicielles**
   - Carte Arduino pour recevoir les informations des capteurs et envoyer les instruction aux capteurs/émetteurs
   - Mise en place d'un webservice qui centralise les différentes requêtes pour 
      - le suivi des capteurs (valeur instantanée et historique)
      - le lancement d'un actionneur
       Utilisation de l'API RESTFul en java. Les services reçoivent des objets en format JSon ou XML
       ce service interagit avec
          - le tensorflow (réception de requêtes HTTP contenant des JSSon pour commander un changement de la température/humidité idéale
          - l'application web pour le suivi de la température et humidité et pour traiter les demandes de changement de température/humidité idéale.
          - la carte Arduino pour recevoir les informations de température et humidité
          - la base de donnée pour historiser les données de capteurs

   - Mise en place d'une base de donnée H2 pour stocker les données de température et d'humidité. (on stock à la fois les données courantes et l'historique)
   - Mise en place d'une client Web (utilisation d'une API RESTFul en java, avec SpringBoot)
     pages web HTML utilisent AngularJS.
     Le client web interroge le webservice

   - Mise en place d'un module de reconnaissance volcale programmé en python et utilisant tensorflow. Ce module sollicite le webservice pour envoyer les commandes de modification de la température/humidité idéale.


![architecture](https://user-images.githubusercontent.com/35843862/144742621-aa5dd341-5880-42fb-ace3-4a8bb0881ffc.gif)


**Description de l'intelligence ajoutée** :

  - Reconnaissance vocale effectuée par le tensorflow


