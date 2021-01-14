#See https://aka.ms/containerfastmode to understand how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/core/aspnet:3.1-buster-slim AS base
WORKDIR /app
EXPOSE 80

FROM mcr.microsoft.com/dotnet/core/sdk:3.1-buster AS build
WORKDIR /src
COPY ["CoVer_Server/CoVer_Server.csproj", "CoVer_Server/"]
RUN dotnet restore "CoVer_Server/CoVer_Server.csproj"
COPY . .
WORKDIR "/src/CoVer_Server"
RUN dotnet build "CoVer_Server.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "CoVer_Server.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "CoVer_Server.dll"]