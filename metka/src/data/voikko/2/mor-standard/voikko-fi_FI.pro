info: Voikko-Dictionary-Format: 2

## Begin-Malaga-Configuration
info: Lex-Version: 3
info: Language-Code: fi_FI
info: Copyright: 2006-2013 Hannu Väisänen, Harri Pitkänen, Teemu Likonen and others
info: License: GPL version 2 or later
info: Language-Variant: standard
info: Description: suomi (perussanasto)
info: SM-Version: 1.14
info: SM-Patchinfo: 
info: Build-Config: GENLEX_OPTS=--style=dialect,foreign,incorrect --min-frequency=10 --extra-usage=sukija,it,medicine,science,education,orgname EXTRA_LEX=vocabulary/erikoisalat/atk-lyhenteet.lex 	vocabulary/erikoisalat/science-misc.lex
info: Build-Date: Thu, 10 Oct 2013 08:13:49 +0000
## End-Malaga-Configuration

## Begin-User-Configuration
lex: voikko-fi_FI.lex suomi.lex
lex: main.lex
mallex: set switch vanhahkot_muodot yes
mallex: set switch vanhat_muodot yes
mallex: set switch sukijan_muodot yes
## End-User-Configuration

## Begin-Internal-Configuration
sym: voikko-fi_FI.sym
all: voikko-fi_FI.all suomi.inc subrule.inc config.inc voikko-fi_FI.pro
lex: suomi.inc subrule.inc voikko-fi_FI.pro
lex: erikoissanat.lex
lex: seikkasanat.lex
lex: suhdesanat.lex
lex: lukusanat.lex
lex: lyhenteet.lex
lex: olla-ei.lex
lex: yhdyssanat.lex
lex: erikoiset.lex
lex: poikkeavat.lex
lex: lainen.lex
lex: taivutustaydennykset.lex
mor: voikko-fi_FI.mor suomi.inc mor.inc subrule.inc config.inc
malaga: set display-line "malshow"
mallex: set display-line "malshow"
mallex: set use-display yes
malaga: set use-display yes
malaga: set mor-pruning 30
mallex: set switch voikko_murre yes
malaga: set mor-incomplete no
## End-Internal-Configuration

