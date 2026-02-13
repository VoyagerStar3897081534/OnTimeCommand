# OnTimeCommand

[![License](https://img.shields.io/github/license/VoyagerStar3897081534/OnTimeCommand)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/releases)
[![Issues](https://img.shields.io/github/issues/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)

Un plugin puissant pour les serveurs Minecraft Paper qui vous permet d'exécuter automatiquement des commandes à des
intervalles de temps définis.

[:cn: 中文](README.md) | [:gb: English](README-en.md) | [:es: Español](README-es.md) | [:ru: Русский](README-ru.md) | [:sa: العربية](README-ar.md)

## 🌟 Fonctionnalités

- ⏰ **Exécution de commandes programmées** - Exécutez automatiquement des commandes Minecraft à des intervalles de temps
  spécifiés
- 🔧 **Gestion flexible des commandes** - Prise en charge de l'ajout, de la suppression, de l'activation et de la
  désactivation des tâches planifiées
- 🎣 **Fonction Orbital TNT** - Fonction spéciale de canne à pêche qui génère des effets d'explosion TNT
- 🔍 **Interface interactive** - Liste de tâches cliquable pour consultation et gestion
- 🛡️ **Système de permissions** - Contrôle granulaire des permissions
- 🔄 **Rechargement à chaud** - Prise en charge du rechargement à chaud des fichiers de configuration sans redémarrage du
  serveur
- 📊 **Surveillance en temps réel** - Affichage de l'état et des détails de toutes les tâches planifiées

## 📋 Configuration système requise

- **Version Minecraft**: 1.21+
- **Serveur**: Paper 1.21+
- **Version Java**: Java 21+

## 🚀 Installation

1. Téléchargez le dernier fichier `.jar`
2. Placez le fichier plugin dans le dossier `plugins` du serveur
3. Redémarrez le serveur
4. Le plugin générera automatiquement les fichiers de configuration

## 📖 Guide d'utilisation

### Commandes de base

#### Commandes de gestion principales

```bash
/ontimecommand <sous-commande> [paramètres...] - Gérer les commandes planifiées (alias : /otc)
/seecommand - Voir toutes les listes de commandes planifiées
/reloadotc - Recharger tous les fichiers de configuration
```

#### Détails des sous-commandes

**Ajouter une nouvelle tâche**

```bash
/ontimecommand add <nom-tâche> <secondes-intervalle>
# Exemple : Ajouter une tâche qui s'exécute toutes les 60 secondes
/ontimecommand add welcome-message 60
```

**Ajouter des commandes à une tâche**

```bash
/ontimecommand addcommand <nom-tâche> <commande1> [commande2] [commande3]...
# Exemple : Ajouter plusieurs commandes à une tâche
/ontimecommand addcommand welcome-message "say Bienvenue !" "title @a title Bienvenue"
```

**Activer/Désactiver les tâches**

```bash
/ontimecommand enable <nom-tâche>
/ontimecommand disable <nom-tâche>
```

**Supprimer des commandes ou des tâches**

```bash
/ontimecommand deletecommand <nom-tâche> <numéro-commande>
/ontimecommand delete <nom-tâche>
```

**Voir les détails de la tâche**

```bash
/ontimecommand seeinfo <nom-tâche>
```

### Nœuds de permission

| Nœud de permission     | Description                                         | Par défaut       |
|------------------------|-----------------------------------------------------|------------------|
| `ontimecommand.admin`  | Utiliser toutes les fonctionnalités d'OnTimeCommand | OP               |
| `ontimecommand.player` | Voir uniquement la liste des commandes              | Tous les joueurs |

### Fichiers de configuration

#### Configuration des commandes planifiées (`on-time-command-list.yml`)

```yaml
commands:
  welcome-message:
    interval: 30
    commands:
      - "say Bienvenue sur le serveur !"
      - "title @a title Bienvenue !"
    disabled: false

  clean-drops:
    interval: 300
    commands:
      - "kill @e[type=item]"
      - "say Objets nettoyés"
    disabled: true
```

#### Configuration Orbital TNT (`orbital-tnt-config.yml`)

```yaml
orbital-tnt:
  enabled: true
  fishing-rod-name: "Orbital TNT"
  wait-time: 5000
  circle-count: 5
  circle-interval: 2
  circle-height: 5
  per-circle-wait-time: 100
```

## 🎣 Fonction Orbital TNT

Lorsque les joueurs utilisent une canne à pêche spéciale nommée "Orbital TNT" pour lancer, des effets d'explosion TNT
seront générés à la position de lancement :

1. Générer un TNT central à la position de lancement
2. Générer plusieurs anneaux TNT autour du point central
3. Chaque anneau est espacé à certaines distances et moments
4. Créer des effets spectaculaires d'explosions en chaîne

## 🔧 Informations développeur

### Construction du projet

```bash
# Cloner le dépôt
git clone https://github.com/VoyagerStar3897081534/OnTimeCommand.git
cd OnTimeCommand

# Construire le projet
mvn clean package
```

### Configuration Maven

Le projet utilise Maven pour la gestion de construction, prenant en charge plusieurs configurations de construction :

```bash
# Publier une version stable
mvn clean package -DversionPackageType=release

# Publier une version bêta
mvn clean package -DversionPackageType=beta

# Version développement (par défaut)
mvn clean package
```

### Structure du projet

```
src/
├── main/
│   ├── java/org/VoyagerStar/onTimeCommand/
│   │   ├── command/
│   │   │   ├── executor/          # Exécuteurs de commandes
│   │   │   └── tabCompleter/      # Complétion de commandes
│   │   ├── init/                  # Module d'initialisation
│   │   ├── listener/              # Écouteurs d'événements
│   │   ├── OnTimeCommand.java     # Classe principale
│   │   └── RunCommandOnTime.java  # Gestion des tâches planifiées
│   └── resources/
│       ├── on-time-command-list.yml
│       ├── orbital-tnt-config.yml
│       └── paper-plugin.yml
└── test/
    └── java/                      # Tests unitaires
```

## 🤝 Lignes directrices de contribution

Bienvenue pour soumettre des Issues et des Pull Requests !

1. Forkez ce dépôt
2. Créez une branche de fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Validez les modifications (`git commit -m 'Add some AmazingFeature'`)
4. Poussez vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrez une Pull Request

## 📝 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails

## 🆘 Support et aide

- 💬 **Retour sur les problèmes** : [GitHub Issues](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)
- 📚 **Documentation** : [Wiki](https://github.com/VoyagerStar3897081534/OnTimeCommand/wiki)
- 📧 **Contacter l'auteur** : VoyagerStar

## 🙏 Remerciements

Merci à tous les développeurs et utilisateurs qui ont contribué à ce projet !

---

<p align="center">
  Fait avec par VoyagerStar
</p>