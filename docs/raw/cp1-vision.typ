#set par(leading: 0.55em, spacing: 0.55em, first-line-indent: 1.8em, justify: true)
#set text(font: "New Computer Modern", size: 14pt, lang: "cs")
#show heading: set block(above: 2em, below: 1em)
#set page(
  margin: (x: 1.25in, y: 1.5in),
  footer: context { align(center, counter(page).display("1")) },
  numbering: "1",
  number-align: center,
)

== Game engine

=== Téma práce
Zvolené téma je 2D game engine ve stylu podobném _dungeon RPG_.

=== Vize projektu
Projekt bude tvořen jednoduchou 2D hrou a herním enginem, který bude umožňovat vytvářet a upravovat herní levely. Hra bude mít jednoduché herní mechaniky, jako pohyb postavy, sbírání předmětů a interakci s prostředím. Hlavní postava by měla být kočka, což by měla reflektovat i tématika hry.

Hra bude obsahovat základní level(y) vhodné pro ukázku herních mechanik, jako je pohyb, souboj s nepřáteli, získávání předmětů, odemikání dalšího postupu apod. Dále bude dostupný level editor, ve kterém bude možné vytvářet a upravovat levely vlastní. To umožní hráčům například i sdílet je s ostatními.

=== Popis funkcionalit
Ve hře se bude hráč pohybovat s hlavní postavou po herním poli. Hráč bude mít možnost sbírat předměty, bojovat s nepřáteli a interagovat s prostředím. Ve hře bude možné například pomocí různých předmětů pozměnit vlastnosti postavy, jako je rychlost, síla, zdraví apod. Nepřátelé budou rozdělení na jednodušší s větším výskytem a težší, které budou mít větší množství zdraví a budou mít silnější útoky. Cílem bude porazit všechny těžké nepřátele (bosse) a dostat se na konec levelu. Během hry bude možné nacházet předměty například v truhlách, nebo si je koupit v herním obchodě. S tím souvisí i možnost sbírání peněz například za zabití nepřátel.

V level editoru bude možné upravit rozložení herního plánu, umístit nepřátele, předměty, truhly, obchody, apod. Dále bude možné nastavit vlastnosti postavy, jako je zdraví, rychlost, síla, apod. Levely půjde do editoru importovat a exportovat z něj, s čímž souvisí vlastní formát, ve kterém budou levely uloženy.

=== Hlavní vlastnosti postavy
- Možnost pohybu pomocí kláves `WASD`
- Možnost interakce truhlami, obchody například pomocí klávesy `E`
- Možnost bojování s nepřáteli například pomocí myši
- Možnost úpravy vlastností (rychlost, síla, zdraví apod.) pomocí různých předmětů

=== Popis předmětů ve hře
Níže je výčet hlavních předmětů (seznam ale není konečný a ze všech částí hry se pravděpodobně bude měnit nejvíce dle potřeby, a tedy slouží hlavně pro představu):
- Peníze - herní měna, kterou lze získat například za zabití nepřátel a za kterou si lze koupit předměty v obchodě
- Maso, granule - jídlo sloužící pro obnovu zdraví
- Pamlsek - jídlo se speciálním efektem
- Meč - zbraň na krátkou vzdálenost (více druhů různých vlastností)
- Brnění - předmět sloužící k redukci zranění (více druhů různých vlastností)
- Páníček - po získání zlepší nějákou vlastnost (např. zdraví)
- ...

#pagebreak()

=== Ovládání, způsob načítání a ukládání
Ovládání bude pomocí klávesnice a myši. Pro import/export levelů bude použit vytvořený formát. To zahrnuje i dosažený postup v jednotlivých levelech, který bude pravděpodobně uložen v jednoduchém formátu typu `JSON/YAML/XML`.

#v(2em)

#figure(
  image("vision.png", width: 100%),
  caption: [Prvotní vize herního plánu a prvků na něm],
)
