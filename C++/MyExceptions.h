#ifndef _MyExceptions_h_
#define _MyExceptions_h_

#include <exception>
using namespace std;

class GImeFajla :public exception
{
public:
	char const* what() const { return "Greska u imenu fajla, pokusajte ponovo!"; }
};

class GNeuspeloOtvaranje :public exception
{
public:
	char const* what() const { return "Nije uspelo otvaranje datog fajla!"; }
};

class GNemaRedo : public exception
{
public:
	char const* what() const { return "Ne postoji dostupna redo opcija!"; }
};

class GNemaUndo :public exception
{
public:
	char const* what() const { return "Ne postoji dostupna undo opcija!"; }
};

class GNepoznataKomanda :public exception
{
public:
	char const* what() const { return "Trazena komanda ne postoji!"; }
};

class GNeodgovarajucaVrednost :public exception
{
public:
	char const* what() const { return "Vrednost ne odgovara trenutnom formatu!"; }
};

class GPostavljanjeDecimala :public exception
{
public:
	char const* what() const { return "Broj decimala se dodeljuje samo numerickom formatu!"; }
};

class GDeljenjeNulom :public exception
{
public:
	char const* what() const { return "Deljenje nulom nije dozvoljeno!"; }
};

class GNepoznatFormat :public exception
{
public:
	char const* what() const { return "Uneti format nije prepoznat!"; }
};

#endif // !_MyExceptions_h_
