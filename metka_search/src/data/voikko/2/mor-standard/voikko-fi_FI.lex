include "suomi.inc";

define @sana1 := <nimisana, tavuviiva>;
define @sana2 := @sana1 + <teonsana>;
define @sisä_ssA_stA := <sisäolento_ssA>;

include "lyhenteet.lex";

# Etuliite (nimisanat)
define @eln := <nimisana>;
# Etuliite (laatusanat)
define @ell := <laatusana>;
# Etuliite (teonsanat)
define @elt := <teonsana>;
# Etuliite (teonsanojen nimi- ja laatusanajohdokset)
define @eltj := <teonsanan_johdoksen_etuliite>;

include "seikkasanat.lex";
include "suhdesanat.lex";
include "erikoiset.lex";
include "poikkeavat.lex";
include "olla-ei.lex";
include "erikoissanat.lex";
include "lukusanat.lex";
include "lainen.lex";
include "taivutustaydennykset.lex";

include "suomi.lex";
include "yhdyssanat.lex";

include "main.lex";

[alku: ".", luokka: lukusana, alaluokka: piste, jatko: <>]; # päivämäärissä
