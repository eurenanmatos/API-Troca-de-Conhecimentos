import React, { useEffect, useState } from 'react';
import './styles/index.css';
import ApiList from './components/ApiList';
import Chart from './components/Chart';

interface UserData {
    id: number;
    nome: string;
    email: string;
    habilidadeOferecida: { nome: string };
    habilidadeDesejada: { nome: string };
}

const App: React.FC = () => {
    const [apiData, setApiData] = useState<{ apis: any[]; chartData: any } | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const loginResponse = await fetch('login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: 'renan@example.com',
                        senha: '1234',
                    }),
                });

                if (!loginResponse.ok) {
                    throw new Error('Não foi possível autenticar no backend.');
                }

                const loginResult = await loginResponse.json();
                const token = loginResult.token;

                const response = await fetch('usuarios', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('Não foi possível buscar os usuários.');
                }

                const result: UserData[] = await response.json();
                const apis = result.map(user => ({
                    id: user.id,
                    name: user.nome,
                    description: `E-mail: ${user.email} • Oferece: ${user.habilidadeOferecida?.nome || 'N/A'}`,
                }));

                setApiData({
                    apis,
                    chartData: {
                        labels: result.map(user => user.nome),
                        datasets: [
                            {
                                label: 'Usuários',
                                data: result.map(() => 1),
                                borderColor: 'rgba(75, 192, 192, 1)',
                                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            },
                        ],
                    },
                });
            } catch (err) {
                setError(err as Error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    return (
        <div className="App container">
            <h1>API Troca de Conhecimentos</h1>
            {loading && <p className="loading">Carregando dados...</p>}
            {error && <p className="error">Erro ao buscar dados: {error.message}</p>}
            {apiData && (
                <>
                    <ApiList data={apiData.apis} />
                    <Chart data={apiData.chartData} />
                </>
            )}
        </div>
    );
};

export default App;
