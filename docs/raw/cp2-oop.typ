#set page(
  margin: (x: 1.25in, y: 1.5in),
  footer: context { align(center, counter(page).display("1")) },
  numbering: "1",
  number-align: center,
)
#show heading: set block(above: 2em, below: 1em)
#set par(leading: 0.55em, spacing: 0.55em, first-line-indent: 1.8em, justify: true)
#set text(font: "New Computer Modern", size: 14pt, lang: "cs")

== Objektový návrh

=== Technologie
Celý projekt je strukturován do dvou částí - UI a Engine. Díky tomu je snažší rozdělit samotné jádro herního enginu a aplikaci, která herní engine využívá.

UI část je tvořená standartní strukturou JavaFX aplikací, tedy hlavní třída App se stará o spuštění aplikace a také o správné ukončení aplikace - vytvoření a zavření okna, zastavení vláken apod. Jednotlivé části aplikace jsou tvořeny .fxml soubory a jejich příslušnými kontrolery.

Engine část má strukturu dělenou na tři části: `core`, `utils`, `gameobjects`. Core obsahuje hlavní jádro enginu, jako je vykreslování, zpracování vstupu uživatele nebo zpracování kolizí. Utils jsou pomocné třídy pro načítání levelů a také assetů (např. obrázky). Gameobjects obsahuje jednotlivé objekty vykreslované ve hře, které mezi sebou mají hierarchické vztahy.

=== Externí knihovny
- JavaFX (`org.openjfx`)
- JSON (`org.json`)

=== Stavy hry
Hra se může nacházet ve třech stavech: nespuštěná, spuštěná a pozastavená.

#pagebreak()
#set page(margin: (x: 0.5in, y: 0.5in))

#figure(
  image("UML.png"),
  caption: [UML diagram],
)
