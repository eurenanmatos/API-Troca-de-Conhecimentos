import React from 'react';
import './styles/index.css';
import ApiList from './components/ApiList';
import Chart from './components/Chart';
import { useFetch } from './hooks/useFetch';

const App: React.FC = () => {
    const { data: apiData, loading, error } = useFetch('/api/endpoint');

    return (
        <div className="App">
            <h1>API Visualization</h1>
            {loading && <p>Loading...</p>}
            {error && <p>Error fetching data: {error.message}</p>}
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