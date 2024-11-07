#include <iostream>
#include "Table.h"
#include <regex>
using namespace std;

//function that starts loading or saving procedure for table tab
int loadingSavingProcedure(Table& tab, string loadSave)
{
	cout << "Unesite ime fajla: ";
	string fajl;
	cin >> fajl;
	int duzina = (int)fajl.length();
	regex r("^(.+)\\.(.{3,4})$");
	smatch matches;
	if (regex_search(fajl, matches, r))
	{
		if (matches[2].str() == "csv")
		{
			tab.perform(loadSave + "-" + matches[1].str() + "-csv");
			return 0;
		}
		else if (matches[2] == "json")
		{
			tab.perform(loadSave + "-" + matches[1].str() + "-json");
			return 0;
		}
		else
		{
			throw GImeFajla();
		}
	}
	else
	{
		throw GImeFajla();
	}
	return 0;
}
//function that retrieves the entered commands and prints options
string menu()
{
	string command="", cell;
	int unos=-1;
	//printing menu
	while (unos < 1 || unos>8)
	{
		cin.sync();
		cout << "1. Unos nove vrednosti" << endl;
		cout << "2. Formatiranje polja/kolone" << endl;
		cout << "3. Napredno upravljanje tabelom" << endl;
		cout << "4. Undo" << endl;
		cout << "5. Redo" << endl;
		cout << "6. Menjanje broja decimala" << endl;
		cout << "7. Cuvanje tabele" << endl;
		cout << "8. Ucitavanje druge tabele" << endl;
		cout << "9. Kraj rada" << endl;
		cout << "Vas unos: ";
		cin >> unos;
		if (!cin) {
			cout << "Morate uneti broj!" << endl;
			cin.clear();
			cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
			continue;
		}
		if (unos < 1 || unos>9) cout << "Opcije su 1-9, uneli ste neadekvatnu vrednost" << endl;
		else break;
	}
	switch (unos)//forming command and passing it to the table which performs it 
	{
	case 1:
		command = "edit-";
		cout << "Unesite polje: ";//uneto nesto sto nije polje
		cin >> cell;
		command += cell +"-";
		cout << "Unesite vrednost: ";
		cin.ignore();
		getline(cin, cell);
		command += cell;
		break;
	case 2:
		command = "format-";
		cout << "Unesite polje/kolonu: ";
		cin >> cell;
		command += cell + "-";
		cout << "Unesite format: ";
		cin >> cell;
		command += cell;
		break;
	case 3:
		command = "advanced";
		break;
	case 4:
		command = "undo";
		break;
	case 5:
		command = "redo";
		break;
	case 6:
		command = "decimals-";
		cout << "Unesite polje: ";//uneto nesto sto nije polje
		cin >> cell;
		command += cell + "-";
		cout << "Unesite broj decimala za prikaz: ";//greska uneto nesto sto nije decimala
		cin >> cell;
		command += cell;
		break;
	case 7:
		command = "save";
		break;
	case 8:
		command = "load";
		break;
	case 9:
		command = "exit";
		break;
	default:
		break;
	}
	return command;
}

int main() {
	Table* tab = tab->getInstance();
	string additionalOutput = "";
	int unos = -1, sacuvana=1;
	//asking if user want new table or to load old one
	try
	{
		while (unos < 1 || unos>2)
		{
			cout << "Da li zelite da radite sa novom tabelom ili da ucitate staru?" << endl;
			cout << "1. Nova" << endl;
			cout << "2. Ucitavanje stare" << endl;
			cout << "Vas unos: ";
			cin >> unos;
			if (!cin) {
				cout << "Morate uneti broj!" << endl;
				cin.clear();
				cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
				continue;
			}
			if (unos < 1 || unos>2) cout << "Opcije su 1/2, uneli ste neadekvatnu vrednost" << endl;
			if (unos == 2)//loading old table
			{
				int ret = loadingSavingProcedure(*tab, "load");
				if (ret != 0) unos = -1;
				else break;
			}
		}
	}
	catch (exception& e)
	{
		cout <<"Greska: "<<e.what() << endl;
	}
	while (1)
	{
		try 
		{
			system("CLS");//clearing output screen
			if(additionalOutput!="") cout << "Greska: " << additionalOutput << endl;
			cout << *tab << endl;//printing table
			string command = menu();//printing menu and getting new command
			if (command == "advanced")//advanced input mode
			{
				bool first = true;
				while (1)
				{
					system("CLS");//clearing output screen
					cout << *tab << endl;//printing table
					if (first) { cin.ignore(); first = false; }//ignores something in input buffer that might affect getline function the first time advanced mode is used
					getline(cin, command);
					//while(command=="")getline(cin, command);
					if (command == "stop") break;
					if (command != "exit") tab->perform(command);
					else break;
				}
			}
			else if (command == "stop") continue;//exiting from advanced input mode
			else if (command == "save")//saving table
			{
				while (1)
				{
					int ret = loadingSavingProcedure(*tab, "save");
					if (ret == 0) break;
				}
			}
			else if (command == "load")//loading another table
			{
				//checking if user want to save old table before loading new one
				if (!tab->isSaved())
				{
					cout << "Da li zelite da sacuvate Vasu tabelu?" << endl;
					char potvrda = 'a';
					while (potvrda != 'D' && potvrda != 'N')
					{
						cout << "(D / N)? ";
						cin >> potvrda;
						if (potvrda != 'D' && potvrda != 'N') cout << "Morate unetu D ili N." << endl;
					}
					if (potvrda == 'D')
					{//save confirmed
						while (1)
						{
							int ret = loadingSavingProcedure(*tab, "save");
							if (ret == 0) break;
						}
					}
				}
				while (1)//loading 
				{
					int ret = loadingSavingProcedure(*tab, "load");
					if (ret == 0) break;
				}
			}
			else if (command != "exit") tab->perform(command);//performing a command
			else//exit procedure
			{
				if (!tab->isSaved())
				{
					cout << "Da li zelite da sacuvate Vasu tabelu?" << endl;
					char potvrda = 'a';
					while (potvrda != 'D' && potvrda != 'N')
					{
						cout << "(D / N)? ";
						cin >> potvrda;
						if (potvrda != 'D' && potvrda != 'N') cout << "Morate unetu D ili N." << endl;
					}
					if (potvrda == 'D')
					{//save confirmed
						while (1)
						{
							int ret = loadingSavingProcedure(*tab, "save");
							if (ret == 0) break;
						}
					}
				}
				free(tab);
				return 0;//end
			}
			additionalOutput = "";
		}
		catch (invalid_argument e)//error handling
		{
			additionalOutput = "Uneli ste neodgovarajucu vrednost!";
		}
		catch (exception& e)//error handling
		{
			additionalOutput = e.what();
		}
	}
	free(tab);
	return 0;//end
}
