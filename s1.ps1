for($i=0;$i -le 5;$i++)
{
 Start-Process -FilePath godotenv -ArgumentList "-f .\local$i.env .\gradlew run"
}